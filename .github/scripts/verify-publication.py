#!/usr/bin/env python3
"""Verify a signed local Maven publication before Central upload."""

import json
import os
import re
import subprocess
import sys
import tempfile
import xml.etree.ElementTree as ET
import zipfile
from pathlib import Path


GROUP = "org.brewcode"
ARTIFACT = "selenide-pages-core"
REPOSITORY_URL = "https://github.com/kochetkov-ma/selenide-pages"
KINDS = ("jar", "sources.jar", "javadoc.jar", "pom", "module")


def require(condition, message):
    if not condition:
        raise ValueError(message)


def names(version):
    base = f"{ARTIFACT}-{version}"
    return {
        "jar": f"{base}.jar",
        "sources.jar": f"{base}-sources.jar",
        "javadoc.jar": f"{base}-javadoc.jar",
        "pom": f"{base}.pom",
        "module": f"{base}.module",
    }


def check_pom(path, version):
    root = ET.parse(path).getroot()
    namespace = {"m": "http://maven.apache.org/POM/4.0.0"}

    def value(key):
        return (root.findtext(key, default="", namespaces=namespace) or "").strip()

    expected = {
        "m:groupId": GROUP,
        "m:artifactId": ARTIFACT,
        "m:version": version,
        "m:url": REPOSITORY_URL,
        "m:licenses/m:license/m:name": "The Apache License, Version 2.0",
        "m:developers/m:developer/m:id": "kochetkov-ma",
        "m:developers/m:developer/m:name": "Maksim Kochetkov",
        "m:developers/m:developer/m:email": "apmatypa88@gmail.com",
        "m:scm/m:url": REPOSITORY_URL,
        "m:scm/m:connection": "scm:git:https://github.com/kochetkov-ma/selenide-pages.git",
    }
    for key, wanted in expected.items():
        require(value(key) == wanted, f"POM {key} mismatch")
    for key in ("m:name", "m:description", "m:licenses/m:license/m:url", "m:scm/m:developerConnection"):
        require(value(key), f"POM {key} missing")


def check_archives(files):
    with zipfile.ZipFile(files["jar"]) as archive:
        entry = "org/brewcode/qa/pages/page/BasePage.class"
        require(entry in archive.namelist(), "Main JAR lacks BasePage.class")
        bytecode = archive.read(entry)
        require(bytecode[:4] == b"\xca\xfe\xba\xbe", "Invalid Java class magic")
        require(int.from_bytes(bytecode[6:8], "big") == 61, "BasePage is not Java 17 bytecode")
    with zipfile.ZipFile(files["sources.jar"]) as archive:
        entries = archive.namelist()
        require(any(name.endswith(".kt") for name in entries), "Sources JAR lacks Kotlin sources")
        require(any(name.endswith(".java") for name in entries), "Sources JAR lacks Java sources")
    with zipfile.ZipFile(files["javadoc.jar"]) as archive:
        require("index.html" in archive.namelist(), "Javadoc JAR lacks index.html")


def check_signatures(pairs, key):
    with tempfile.TemporaryDirectory(prefix="publication-gpg-") as home:
        os.chmod(home, 0o700)
        command = ["gpg", "--batch", "--no-tty", "--homedir", home]
        try:
            imported = subprocess.run(command + ["--import"], input=key.encode(), stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=False)
            require(imported.returncode == 0, "Signing key import failed")
            for signature, artifact in pairs:
                checked = subprocess.run(command + ["--verify", str(signature), str(artifact)], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=False)
                require(checked.returncode == 0, f"Invalid signature: {signature.name}")
        finally:
            subprocess.run(["gpgconf", "--homedir", home, "--kill", "gpg-agent"], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL, check=False)


def inspect(build, local, version, key):
    require(re.fullmatch(r"[0-9]+\.[0-9]+\.[0-9]+(?:-[A-Za-z0-9][A-Za-z0-9.-]*)?", version), "Invalid publication version")
    require(key, "Signing key unavailable")
    named = names(version)
    files = {kind: local / filename for kind, filename in named.items()}
    originals = {
        "jar": build / "libs" / named["jar"],
        "sources.jar": build / "libs" / named["sources.jar"],
        "javadoc.jar": build / "libs" / named["javadoc.jar"],
        "pom": build / "publications/maven/pom-default.xml",
        "module": build / "publications/maven/module.json",
    }
    pairs = []
    for kind in KINDS:
        artifact = files[kind]
        original = originals[kind]
        signature = original.with_name(original.name + ".asc")
        require(artifact.is_file() and artifact.stat().st_size > 0, f"Local artifact absent: {artifact.name}")
        require(original.is_file() and original.stat().st_size > 0, f"Built artifact absent: {original.name}")
        require(artifact.read_bytes() == original.read_bytes(), f"Built and local artifacts differ: {artifact.name}")
        require(signature.is_file() and signature.stat().st_size > 0, f"Signature absent: {signature.name}")
        pairs.append((signature, artifact))
    check_pom(files["pom"], version)
    module = json.loads(files["module"].read_text())
    component = module.get("component", {})
    require(all(component.get(key) == value for key, value in (("group", GROUP), ("module", ARTIFACT), ("version", version))), "Gradle module coordinates mismatch")
    check_archives(files)
    check_signatures(pairs, key)


def main():
    require(len(sys.argv) == 2, "Usage: verify-publication.py VERSION")
    version = sys.argv[1]
    root = Path(__file__).resolve().parents[2]
    build = root / "selenide-pages-core/build"
    local = Path.home() / ".m2/repository/org/brewcode/selenide-pages-core" / version
    inspect(build, local, version, os.environ.get("ORG_GRADLE_PROJECT_signingInMemoryKey", ""))
    print(f"Signed local publication verified: {GROUP}:{ARTIFACT}:{version}")


if __name__ == "__main__":
    try:
        main()
    except (ValueError, OSError, ET.ParseError, zipfile.BadZipFile, json.JSONDecodeError) as error:
        print(f"Local publication verification failed: {error}", file=sys.stderr)
        sys.exit(1)
