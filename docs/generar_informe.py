#!/usr/bin/env python3
"""Genera el informe PDF de la actividad a partir de docs/informe.md.

El documento conserva una sola fuente de contenido (Markdown) y agrega la
portada, el índice, los encabezados, la evidencia de ejecución y el pie de
página. Requiere reportlab.
"""

from __future__ import annotations

import re
import sys
from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.units import cm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (
    BaseDocTemplate,
    Frame,
    HRFlowable,
    NextPageTemplate,
    PageBreak,
    PageTemplate,
    Paragraph,
    Preformatted,
    Spacer,
    Table,
    TableStyle,
)
from reportlab.platypus.tableofcontents import TableOfContents

RAIZ = Path(__file__).resolve().parent.parent
INFORME_MD = RAIZ / "docs" / "informe.md"
EVIDENCIA = RAIZ / "docs" / "entregables" / "evidencia-ejecucion.txt"
SALIDA = RAIZ / "docs" / "entregables" / "Informe-Actividad-U2.pdf"

AZUL = colors.HexColor("#17365D")
AZUL_CLARO = colors.HexColor("#2E5C8A")
GRIS_TEXTO = colors.HexColor("#3B4652")
GRIS_SUAVE = colors.HexColor("#6B7A89")
GRIS_LINEA = colors.HexColor("#C9D4DF")
FONDO_CODIGO = colors.HexColor("#F4F7FA")
FONDO_TABLA = colors.HexColor("#F2F6FA")
DORADO = colors.HexColor("#C9A227")

ANCHO_UTIL = A4[0] - 4 * cm


def registrar_fuentes() -> tuple[str, str, str, str, str]:
    """Registra Arial y Consolas; si no existen, usa las fuentes base."""
    fuentes = Path(r"C:\Windows\Fonts")
    try:
        pdfmetrics.registerFont(TTFont("InformeArial", str(fuentes / "arial.ttf")))
        pdfmetrics.registerFont(TTFont("InformeArial-Bold", str(fuentes / "arialbd.ttf")))
        pdfmetrics.registerFont(TTFont("InformeArial-Italic", str(fuentes / "ariali.ttf")))
        pdfmetrics.registerFont(TTFont("InformeArial-BoldItalic", str(fuentes / "arialbi.ttf")))
        pdfmetrics.registerFont(TTFont("InformeConsolas", str(fuentes / "consola.ttf")))
        pdfmetrics.registerFontFamily(
            "InformeArial",
            normal="InformeArial",
            bold="InformeArial-Bold",
            italic="InformeArial-Italic",
            boldItalic="InformeArial-BoldItalic",
        )
        return (
            "InformeArial",
            "InformeArial-Bold",
            "InformeArial-Italic",
            "InformeArial-BoldItalic",
            "InformeConsolas",
        )
    except Exception:
        return ("Helvetica", "Helvetica-Bold", "Helvetica-Oblique",
                "Helvetica-BoldOblique", "Courier")


FUENTE, FUENTE_NEGRITA, FUENTE_CURSIVA, FUENTE_NEGRITA_CURSIVA, FUENTE_CODIGO = registrar_fuentes()


def estilos() -> dict[str, ParagraphStyle]:
    base = dict(fontName=FUENTE, fontSize=9.8, leading=14.2, textColor=GRIS_TEXTO)
    return {
        "Titulo": ParagraphStyle(
            "Titulo", fontName=FUENTE_NEGRITA, fontSize=25, leading=29,
            textColor=AZUL, alignment=TA_LEFT, spaceAfter=6,
        ),
        "Subtitulo": ParagraphStyle(
            "Subtitulo", fontName=FUENTE, fontSize=13.5, leading=18,
            textColor=AZUL_CLARO, alignment=TA_LEFT, spaceAfter=4,
        ),
        "Etiqueta": ParagraphStyle(
            "Etiqueta", fontName=FUENTE_NEGRITA, fontSize=10, leading=13,
            textColor=AZUL, alignment=TA_LEFT,
        ),
        "H1": ParagraphStyle(
            "H1", fontName=FUENTE_NEGRITA, fontSize=17, leading=21,
            textColor=AZUL, spaceBefore=16, spaceAfter=8, keepWithNext=True,
        ),
        "H2": ParagraphStyle(
            "H2", fontName=FUENTE_NEGRITA, fontSize=12.8, leading=16,
            textColor=AZUL, spaceBefore=12, spaceAfter=5, keepWithNext=True,
        ),
        "H3": ParagraphStyle(
            "H3", fontName=FUENTE_NEGRITA, fontSize=10.8, leading=14,
            textColor=AZUL_CLARO, spaceBefore=9, spaceAfter=3, keepWithNext=True,
        ),
        "Body": ParagraphStyle("Body", alignment=TA_JUSTIFY, spaceAfter=5, **base),
        "Bullet": ParagraphStyle(
            "Bullet", alignment=TA_JUSTIFY, leftIndent=14, bulletIndent=3,
            spaceAfter=2.5, **base,
        ),
        "Numero": ParagraphStyle(
            "Numero", alignment=TA_JUSTIFY, leftIndent=17, bulletIndent=2,
            spaceAfter=3.5, **base,
        ),
        "Quote": ParagraphStyle(
            "Quote", fontName=FUENTE_CURSIVA, fontSize=9.3, leading=13,
            textColor=GRIS_SUAVE, leftIndent=14, rightIndent=14,
            spaceBefore=4, spaceAfter=6,
        ),
        "TableHeader": ParagraphStyle(
            "TableHeader", fontName=FUENTE_NEGRITA, fontSize=8.2, leading=10.5,
            textColor=colors.white, alignment=TA_LEFT,
        ),
        "TableCell": ParagraphStyle(
            "TableCell", fontName=FUENTE, fontSize=8.2, leading=10.6,
            textColor=GRIS_TEXTO, alignment=TA_LEFT,
        ),
        "Code": ParagraphStyle(
            "Code", fontName=FUENTE_CODIGO, fontSize=6.4, leading=7.7,
            textColor=colors.HexColor("#1F2D3A"),
        ),
        "TOC": ParagraphStyle(
            "TOC", fontName=FUENTE_NEGRITA, fontSize=15, leading=19,
            textColor=AZUL, spaceAfter=10,
        ),
        "Pie": ParagraphStyle(
            "Pie", fontName=FUENTE, fontSize=8, leading=10,
            textColor=GRIS_SUAVE, alignment=TA_CENTER,
        ),
    }


def inline(texto: str) -> str:
    """Convierte el subconjunto de Markdown usado a marcado de reportlab."""
    texto = texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
    texto = re.sub(
        r"&lt;(https?://[^&]+?)&gt;",
        r'<link href="\1" color="#17365D">\1</link>',
        texto,
    )
    texto = re.sub(
        r"\[([^\]]+)\]\(([^)]+)\)",
        r'<link href="\2" color="#17365D">\1</link>',
        texto,
    )
    texto = re.sub(r"\*\*(.+?)\*\*", r"<b>\1</b>", texto)
    texto = re.sub(
        r"`([^`]+)`",
        r'<font name="' + FUENTE_CODIGO + r'" size="8.5">\1</font>',
        texto,
    )
    texto = re.sub(r"(?<!\*)\*([^*\n]+)\*(?!\*)", r"<i>\1</i>", texto)
    return texto


def partir_linea(linea: str, limite: int = 128) -> list[str]:
    """Ajusta una línea larga al ancho del bloque, cortando por palabras."""
    if len(linea) <= limite:
        return [linea if linea else " "]
    partes: list[str] = []
    resto = linea
    while len(resto) > limite:
        corte = resto.rfind(" ", 0, limite + 1)
        if corte <= 0:
            corte = limite
        partes.append(resto[:corte].rstrip())
        resto = "    " + resto[corte:].lstrip()
    partes.append(resto)
    return partes


def bloque_codigo(codigo: str, estilo: ParagraphStyle, ancho: float = ANCHO_UTIL) -> Table:
    lineas: list[str] = []
    for linea in codigo.rstrip("\n").splitlines():
        lineas.extend(partir_linea(linea))
    contenido = Preformatted("\n".join(lineas), estilo)
    tabla = Table([[contenido]], colWidths=[ancho])
    tabla.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, -1), FONDO_CODIGO),
        ("BOX", (0, 0), (-1, -1), 0.5, GRIS_LINEA),
        ("LEFTPADDING", (0, 0), (-1, -1), 7),
        ("RIGHTPADDING", (0, 0), (-1, -1), 7),
        ("TOPPADDING", (0, 0), (-1, -1), 6),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 6),
    ]))
    return tabla


def bloque_tabla(lineas: list[str], estilos_pdf: dict[str, ParagraphStyle]) -> Table:
    filas = []
    for linea in lineas:
        celdas = [c.strip() for c in linea.strip().strip("|").split("|")]
        filas.append(celdas)
    if len(filas) >= 2 and all(re.fullmatch(r":?-{3,}:?", c) for c in filas[1]):
        encabezado, cuerpo = filas[0], filas[2:]
    else:
        encabezado, cuerpo = filas[0], filas[1:]
    columnas = max(len(f) for f in filas)
    datos = []
    datos.append([
        Paragraph(inline(c) if c else " ", estilos_pdf["TableHeader"])
        for c in encabezado + [""] * (columnas - len(encabezado))
    ])
    for fila in cuerpo:
        datos.append([
            Paragraph(inline(c) if c else " ", estilos_pdf["TableCell"])
            for c in fila + [""] * (columnas - len(fila))
        ])
    tabla = Table(datos, colWidths=[ANCHO_UTIL / columnas] * columnas, repeatRows=1)
    tabla.setStyle(TableStyle([
        ("BACKGROUND", (0, 0), (-1, 0), AZUL),
        ("GRID", (0, 0), (-1, -1), 0.4, GRIS_LINEA),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("LEFTPADDING", (0, 0), (-1, -1), 5),
        ("RIGHTPADDING", (0, 0), (-1, -1), 5),
        ("TOPPADDING", (0, 0), (-1, -1), 4),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
        ("ROWBACKGROUNDS", (0, 1), (-1, -1), [colors.white, FONDO_TABLA]),
    ]))
    return tabla


def convertir_markdown(texto: str, estilos_pdf: dict[str, ParagraphStyle]) -> list:
    lineas = texto.splitlines()
    historia: list = []
    parrafo: list[str] = []

    def vaciar_parrafo() -> None:
        if parrafo:
            historia.append(Paragraph(inline(" ".join(p.strip() for p in parrafo)),
                                      estilos_pdf["Body"]))
            parrafo.clear()

    i = 0
    while i < len(lineas):
        linea = lineas[i]

        if linea.startswith("```"):
            vaciar_parrafo()
            i += 1
            codigo: list[str] = []
            while i < len(lineas) and not lineas[i].startswith("```"):
                codigo.append(lineas[i])
                i += 1
            i += 1
            historia.append(bloque_codigo("\n".join(codigo), estilos_pdf["Code"]))
            historia.append(Spacer(1, 4))
            continue

        if linea.strip().startswith("|"):
            vaciar_parrafo()
            tabla_lineas: list[str] = []
            while i < len(lineas) and lineas[i].strip().startswith("|"):
                tabla_lineas.append(lineas[i])
                i += 1
            historia.append(bloque_tabla(tabla_lineas, estilos_pdf))
            historia.append(Spacer(1, 5))
            continue

        if linea.startswith("#"):
            vaciar_parrafo()
            nivel = len(linea) - len(linea.lstrip("#"))
            titulo = linea[nivel:].strip()
            estilo = {1: "H1", 2: "H2", 3: "H3"}.get(nivel, "H3")
            historia.append(Paragraph(inline(titulo), estilos_pdf[estilo]))
            i += 1
            continue

        if linea.strip() == "---":
            vaciar_parrafo()
            historia.append(HRFlowable(width="100%", thickness=0.6, color=GRIS_LINEA,
                                       spaceBefore=4, spaceAfter=8))
            i += 1
            continue

        if linea.startswith(">"):
            vaciar_parrafo()
            cita: list[str] = []
            while i < len(lineas) and lineas[i].startswith(">"):
                cita.append(lineas[i].lstrip("> ").strip())
                i += 1
            historia.append(Paragraph(inline(" ".join(cita)), estilos_pdf["Quote"]))
            continue

        coincidencia = re.match(r"^\s*[-*]\s+(.*)$", linea)
        if coincidencia:
            vaciar_parrafo()
            contenido = coincidencia.group(1)
            i += 1
            while i < len(lineas):
                siguiente = lineas[i]
                if not siguiente.strip() or re.match(r"^\s*([-*]|\d+\.)\s+", siguiente):
                    break
                if siguiente.startswith((" ", "\t")):
                    contenido += " " + siguiente.strip()
                    i += 1
                else:
                    break
            historia.append(Paragraph(inline(contenido), estilos_pdf["Bullet"], bulletText="•"))
            continue

        coincidencia = re.match(r"^\s*(\d+)\.\s+(.*)$", linea)
        if coincidencia:
            vaciar_parrafo()
            numero = coincidencia.group(1)
            contenido = coincidencia.group(2)
            i += 1
            while i < len(lineas):
                siguiente = lineas[i]
                if not siguiente.strip() or re.match(r"^\s*([-*]|\d+\.)\s+", siguiente):
                    break
                if siguiente.startswith((" ", "\t")):
                    contenido += " " + siguiente.strip()
                    i += 1
                else:
                    break
            historia.append(Paragraph(inline(contenido), estilos_pdf["Numero"],
                                      bulletText=numero + "."))
            continue

        if not linea.strip():
            vaciar_parrafo()
        else:
            parrafo.append(linea)
        i += 1

    vaciar_parrafo()
    return historia


class InformeDocTemplate(BaseDocTemplate):
    """Documento con portada sin encabezado y páginas de contenido numeradas."""

    def __init__(self, nombre: str, **kwargs):
        super().__init__(nombre, **kwargs)
        marco_portada = Frame(
            2 * cm, 1.9 * cm, A4[0] - 4 * cm, A4[1] - 8.3 * cm,
            id="marco-portada", leftPadding=0, rightPadding=0,
            topPadding=0, bottomPadding=0,
        )
        marco_contenido = Frame(
            2 * cm, 1.9 * cm, A4[0] - 4 * cm, A4[1] - 4.1 * cm,
            id="marco-contenido", leftPadding=0, rightPadding=0,
            topPadding=0, bottomPadding=0,
        )
        self.addPageTemplates([
            PageTemplate(id="portada", frames=[marco_portada], onPage=self._portada),
            PageTemplate(id="contenido", frames=[marco_contenido], onPage=self._contenido),
        ])

    def _portada(self, lienzo, documento) -> None:
        lienzo.saveState()
        lienzo.setFillColor(AZUL)
        lienzo.rect(0, A4[1] - 5.8 * cm, A4[0], 5.8 * cm, stroke=0, fill=1)
        lienzo.setFillColor(DORADO)
        lienzo.rect(0, A4[1] - 5.95 * cm, A4[0], 0.15 * cm, stroke=0, fill=1)
        lienzo.setFillColor(colors.white)
        lienzo.setFont(FUENTE_NEGRITA, 17)
        lienzo.drawCentredString(A4[0] / 2, A4[1] - 2.1 * cm, "UNIVERSIDAD DE CARTAGENA")
        lienzo.setFont(FUENTE, 10.5)
        lienzo.drawCentredString(A4[0] / 2, A4[1] - 2.9 * cm,
                                 "Facultad de Ingeniería · Programa de Ingeniería de Software")
        lienzo.setFont(FUENTE, 10)
        lienzo.drawCentredString(A4[0] / 2, A4[1] - 3.6 * cm,
                                 "Estructuras de Datos · Actividad de Aprendizaje Unidad 2")
        lienzo.setStrokeColor(colors.white)
        lienzo.setLineWidth(0.5)
        lienzo.line(4 * cm, A4[1] - 4.2 * cm, A4[0] - 4 * cm, A4[1] - 4.2 * cm)
        lienzo.setFont(FUENTE, 9)
        lienzo.drawCentredString(A4[0] / 2, A4[1] - 4.9 * cm,
                                 "Caso 19 · Taller de computadores")
        lienzo.setFillColor(GRIS_SUAVE)
        lienzo.setFont(FUENTE, 8.5)
        lienzo.drawCentredString(A4[0] / 2, 1.25 * cm,
                                 "Cartagena de Indias · 2026")
        lienzo.restoreState()

    def _contenido(self, lienzo, documento) -> None:
        lienzo.saveState()
        lienzo.setFont(FUENTE, 7.6)
        lienzo.setFillColor(GRIS_SUAVE)
        lienzo.drawString(2 * cm, A4[1] - 1.3 * cm,
                          "Estructuras de Datos · Unidad 2")
        lienzo.drawRightString(A4[0] - 2 * cm, A4[1] - 1.3 * cm,
                               "Caso 19 · Taller de computadores")
        lienzo.setStrokeColor(GRIS_LINEA)
        lienzo.setLineWidth(0.6)
        lienzo.line(2 * cm, A4[1] - 1.55 * cm, A4[0] - 2 * cm, A4[1] - 1.55 * cm)
        lienzo.setFont(FUENTE, 7.6)
        lienzo.drawCentredString(A4[0] / 2, 1.15 * cm, f"Página {documento.page}")
        lienzo.restoreState()

    def afterFlowable(self, flowable) -> None:
        if not isinstance(flowable, Paragraph):
            return
        estilo = flowable.style.name
        if estilo in ("H1", "H2"):
            nivel = 0
        elif estilo == "H3":
            nivel = 1
        else:
            return
        self.notify("TOCEntry", (nivel, flowable.getPlainText(), self.page))


def portada(estilos_pdf: dict[str, ParagraphStyle]) -> list:
    datos = [
        ("Asignatura", "Estructuras de Datos"),
        ("Programa", "Ingeniería de Software"),
        ("Docente", "John Carlos Arrieta Arrieta"),
        ("Estudiante", "Juan José Puello Valdez"),
        ("Código", "7502510043"),
        ("Fecha de entrega", "14 de noviembre de 2026"),
    ]
    tabla_datos = Table(
        [[Paragraph(etiqueta, estilos_pdf["Etiqueta"]), Paragraph(valor, estilos_pdf["Body"])]
         for etiqueta, valor in datos],
        colWidths=[4.2 * cm, 11.3 * cm],
    )
    tabla_datos.setStyle(TableStyle([
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("LINEBELOW", (0, 0), (-1, -1), 0.35, GRIS_LINEA),
        ("TOPPADDING", (0, 0), (-1, -1), 4),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 4),
        ("LEFTPADDING", (0, 0), (-1, -1), 0),
    ]))
    return [
        Spacer(1, 0.9 * cm),
        Paragraph("Sistema de gestión de reparaciones", estilos_pdf["Titulo"]),
        Paragraph("Lista, Cola y Pila aplicadas a un taller de computadores",
                  estilos_pdf["Subtitulo"]),
        Spacer(1, 0.35 * cm),
        HRFlowable(width="100%", thickness=1.2, color=DORADO, spaceAfter=10),
        Paragraph("Caso 19 · Entidad EquipoReparacion · Identificador: código de servicio",
                  estilos_pdf["Etiqueta"]),
        Spacer(1, 1.1 * cm),
        tabla_datos,
        Spacer(1, 1.1 * cm),
        Paragraph(
            'Repositorio: <link href="https://github.com/JuanJoPV01/sistema-taller-computadores" '
            'color="#17365D">github.com/JuanJoPV01/sistema-taller-computadores</link>',
            estilos_pdf["Body"],
        ),
        Spacer(1, 0.25 * cm),
        Paragraph(
            "El informe describe el diseño, la implementación y las pruebas del sistema, "
            "junto con la evidencia de ejecución y los enlaces de entrega.",
            estilos_pdf["Body"],
        ),
    ]


def historia_evidencia(estilos_pdf: dict[str, ParagraphStyle]) -> list:
    if not EVIDENCIA.exists():
        return []
    texto = EVIDENCIA.read_text(encoding="utf-8-sig").splitlines()
    historia: list = [PageBreak(),
                      Paragraph("Salida completa de la demostración y las pruebas",
                                estilos_pdf["H3"]),
                      Spacer(1, 4)]
    tamano = 40
    for inicio in range(0, len(texto), tamano):
        trozo = "\n".join(texto[inicio:inicio + tamano])
        historia.append(bloque_codigo(trozo, estilos_pdf["Code"]))
        historia.append(Spacer(1, 3))
    return historia


def main() -> int:
    if not INFORME_MD.exists():
        print(f"No se encontró {INFORME_MD}", file=sys.stderr)
        return 1
    SALIDA.parent.mkdir(parents=True, exist_ok=True)
    texto = INFORME_MD.read_text(encoding="utf-8")
    if texto.startswith("# "):
        partes = texto.split("\n---\n", 1)
        if len(partes) == 2:
            texto = partes[1]

    estilos_pdf = estilos()
    historia = portada(estilos_pdf)
    historia += [NextPageTemplate("contenido"), PageBreak()]

    historia.append(Paragraph("Contenido", estilos_pdf["TOC"]))
    indice = TableOfContents()
    indice.levelStyles = [
        ParagraphStyle("TOCNivel0", fontName=FUENTE_NEGRITA, fontSize=9.6,
                       leading=14, textColor=AZUL, spaceBefore=1),
        ParagraphStyle("TOCNivel1", fontName=FUENTE, fontSize=8.8,
                       leading=12, textColor=GRIS_TEXTO, leftIndent=14),
    ]
    historia.append(indice)
    historia.append(PageBreak())

    historia += convertir_markdown(texto, estilos_pdf)
    historia += historia_evidencia(estilos_pdf)

    documento = InformeDocTemplate(
        str(SALIDA),
        pagesize=A4,
        leftMargin=2 * cm,
        rightMargin=2 * cm,
        topMargin=2.1 * cm,
        bottomMargin=1.9 * cm,
        title="Actividad Unidad 2 - Sistema de gestión con Lista, Cola y Pila",
        author="Juan José Puello Valdez",
        subject="Estructuras de Datos - Caso 19 Taller de computadores",
        creator="generar_informe.py",
    )
    documento.multiBuild(historia)
    print(f"PDF generado: {SALIDA}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
