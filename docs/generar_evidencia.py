#!/usr/bin/env python3
"""Genera la evidencia de ejecución capturando los bytes UTF-8 de Java.

PowerShell puede reinterpretar la salida nativa y dañar algunas tildes. Por
eso este script ejecuta las clases compiladas con ``subprocess`` y escribe
directamente los bytes producidos por la JVM, sin pasarlos por la consola.
"""

from __future__ import annotations

import datetime as dt
import subprocess
import sys
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
CLASES = RAIZ / "out" / "entregables"
DESTINO = RAIZ / "docs" / "entregables" / "evidencia-ejecucion.txt"


def ejecutar(clase: str, *argumentos: str) -> bytes:
    comando = [
        "java",
        "-Dfile.encoding=UTF-8",
        "-Dstdout.encoding=UTF-8",
        "-cp",
        str(CLASES),
        clase,
        *argumentos,
    ]
    resultado = subprocess.run(comando, capture_output=True)
    if resultado.returncode != 0:
        sys.stderr.buffer.write(resultado.stderr)
        raise SystemExit(f"La clase {clase} terminó con código {resultado.returncode}.")
    return resultado.stdout


def main() -> int:
    if not CLASES.exists():
        print(f"No existe el directorio de clases: {CLASES}", file=sys.stderr)
        return 1
    DESTINO.parent.mkdir(parents=True, exist_ok=True)

    demostracion = ejecutar("edu.unicartagena.taller.SistemaDelCaso", "--demo")
    pruebas = ejecutar("edu.unicartagena.taller.PruebasTaller")
    fecha = dt.datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    cabecera = (
        "================================================================\n"
        "EVIDENCIA DE EJECUCIÓN - ACTIVIDAD UNIDAD 2 - CASO 19\n"
        "Sistema de gestión de reparaciones - Taller de computadores\n"
        "Estudiante: Juan José Puello Valdez - Código: 7502510043\n"
        f"Fecha de generación: {fecha}\n"
        "================================================================\n\n"
        ">>> DEMOSTRACIÓN AUTOMÁTICA (java ... --demo)\n"
    ).encode("utf-8")

    contenido = (
        cabecera
        + demostracion
        + "\n>>> PRUEBAS AUTOMÁTICAS (PruebasTaller)\n".encode("utf-8")
        + pruebas
        + b"\n"
    )
    DESTINO.write_bytes(contenido)
    print(f"Evidencia generada: {DESTINO}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
