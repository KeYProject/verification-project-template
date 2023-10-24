#!/usr/bin/python

import sys
import re
from pathlib import Path


class Mode(metaclass=enum.Enum):
    JAVA = 0
    JAVA_SL_COMMENT = 1
    JAVA_ML_COMMENT = 2

    JML_ML = 4
    JML_SL_COMMENT = 5
    JML_ML_COMMENT = 6
    JML_SL = 7


class Stat:
    jmlLines: int  = 0
    javaLines: int = 0
    documentation: int = 0


def count(file: Path):
    with file.open() as fh:
        count_str(fh.read())


JML_SL_COMMENT_START = re.compile(r"^//([+-]\w*)*@")
JML_ML_COMMENT_START = re.compile(r"^/*([+-]\w*)*@")

def count_str(s: str):
    stat = Stat()

    def _count(val):
        for ch in val:
            if ch =='\n': stat.jmlLines += 1
    def consume_until(*args):
        nonlocal s
        m = min((s.find(n) for n in args))
        if m > 0:
            _count(s[:m])
            s = s[m:]

    def lookat_jml_ml(): return bool(JML_ML_COMMENT_START.matches(s))
    def lookat_jml_sl(): return bool(JML_SL_COMMENT_START.matches(s))
    def lookat_comment_ml(): return s.startswith("/*")
    def lookat_comment_sl(): return s.startswith("//")
    def lookat_comment_end_ml(): return s.startswith("*/")

    def consume():
        nonlocal s
        s = s[1:]

    mode = Mode.JAVA
    while s != "":
        if mode == Mode.JAVA:
            consume_until("//", "/*")
            if lookat_jml_ml(): mode = Mode.JML_ML
            elif lookat_jml_sl(): mode = Mode.JML_SL
            elif lookat_comment_ml(): mode = Mode.JAVA_ML_COMMENT
            elif lookat_comment_sl(): mode = Mode.JAVA_SL_COMMENT
        elif mode == Mode.JML_ML:
            consume_until("//", "*/")
            if lookat_comment_sl(): mode = Mode.JML_SL_COMMENT
            elif lookat_comment_end_ml(): mode = Mode.JAVA
        elif mode == Mode.JAVA_ML_COMMENT:
            consume_until( "*/")
            if lookat_comment_sl(): mode = Mode.JML_SL_COMMENT
            elif lookat_comment_end_ml(): mode = Mode.JAVA
        elif mode == Mode.JAVA_SL_COMMENT:
            consume_until("\n")
            if lookat_comment_sl(): mode = Mode.JAVA
            elif lookat_comment_end_sl(): consume(); mode = Mode.JAVA
        elif mode == Mode.JML_ML_COMMENT:
            consume_until( "*/")
            if lookat_comment_sl(): mode = Mode.JML_SL_COMMENT
            elif lookat_comment_end_ml(): mode = Mode.JAVA
        elif mode == Mode.JML_SL_COMMENT:
            consume_until("\n")
            if lookat_comment_sl(): mode = Mode.JAVA
            elif lookat_comment_end_sl(): consume(); mode = Mode.JAVA


