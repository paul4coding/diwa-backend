package Tg.OSEOR.DIWA.Backend.service.document;

import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import Tg.OSEOR.DIWA.Backend.entity.LigneProFormaMainOeuvre;
import Tg.OSEOR.DIWA.Backend.entity.LigneProFormaTravail;
import Tg.OSEOR.DIWA.Backend.entity.ProForma;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class DocumentGenerationService {

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fr", "TG"));

    public byte[] genererPdf(ProForma pf, String version) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // En-tête
        document.add(new Paragraph("DIWA INTERNATIONALE").setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("PRO FORMA - " + pf.getReference()).setBold().setFontSize(16).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Version : " + version).setFontSize(10).setTextAlignment(TextAlignment.RIGHT));
        document.add(new Paragraph("\n"));

        // Infos Client / Véhicule
        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
        infoTable.addCell(new Cell().add(new Paragraph("Client : " + pf.getDemande().getClient().getUsername())));
        infoTable.addCell(new Cell().add(new Paragraph("Véhicule : " + pf.getDemande().getVehiculeMarque() + " " + pf.getDemande().getVehiculeModele())));
        infoTable.addCell(new Cell().add(new Paragraph("Immatriculation : " + pf.getDemande().getVehiculeImmatriculation())));
        infoTable.addCell(new Cell().add(new Paragraph("Kilométrage : " + pf.getDemande().getVehiculeKilometrage() + " KM")));
        document.add(infoTable);
        document.add(new Paragraph("\n"));

        // Tableau Travaux
        document.add(new Paragraph("TRAVAUX ET PIÈCES").setBold());
        boolean displayPrices = !version.equalsIgnoreCase("TECHNICIEN");
        float[] columnWidths = displayPrices ? new float[]{40, 20, 20, 20} : new float[]{80, 20};
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();
        
        table.addCell(new Cell().add(new Paragraph("Désignation")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addCell(new Cell().add(new Paragraph("Qté")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        if (displayPrices) {
            table.addCell(new Cell().add(new Paragraph("P.U.")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addCell(new Cell().add(new Paragraph("Total")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        }

        for (LigneProFormaTravail l : pf.getLignesTravaux()) {
            if (l.getCocheeParClient()) {
                table.addCell(new Cell().add(new Paragraph(l.getDesignation())));
                table.addCell(new Cell().add(new Paragraph(l.getQuantite().toString())));
                if (displayPrices) {
                    table.addCell(new Cell().add(new Paragraph(formatCurrency(l.getPrixUnitaire()))));
                    table.addCell(new Cell().add(new Paragraph(formatCurrency(l.getPrixTotal()))));
                }
            }
        }
        document.add(table);
        document.add(new Paragraph("\n"));

        // Tableau Main d'Oeuvre
        document.add(new Paragraph("MAIN D'OEUVRE").setBold());
        float[] moWidths = displayPrices ? new float[]{40, 20, 20, 20} : new float[]{80, 20};
        Table moTable = new Table(UnitValue.createPercentArray(moWidths)).useAllAvailableWidth();
        moTable.addCell(new Cell().add(new Paragraph("Type Intervention")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        moTable.addCell(new Cell().add(new Paragraph("Heures")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        if (displayPrices) {
            moTable.addCell(new Cell().add(new Paragraph("Taux H.")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
            moTable.addCell(new Cell().add(new Paragraph("Total")).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        }

        for (LigneProFormaMainOeuvre l : pf.getLignesMainOeuvre()) {
            if (l.getCocheeParClient()) {
                moTable.addCell(new Cell().add(new Paragraph(l.getTypeIntervention())));
                moTable.addCell(new Cell().add(new Paragraph(l.getHeures().toString())));
                if (displayPrices) {
                    moTable.addCell(new Cell().add(new Paragraph(formatCurrency(l.getTauxHoraire()))));
                    moTable.addCell(new Cell().add(new Paragraph(formatCurrency(l.getTotal()))));
                }
            }
        }
        document.add(moTable);
        document.add(new Paragraph("\n"));

        // Totaux
        if (displayPrices) {
            Table totalTable = new Table(UnitValue.createPercentArray(new float[]{70, 30})).useAllAvailableWidth();
            totalTable.addCell(new Cell().add(new Paragraph("TOTAL PIÈCES")).setBold());
            totalTable.addCell(new Cell().add(new Paragraph(formatCurrency(pf.getTotalPieces()))).setTextAlignment(TextAlignment.RIGHT));
            totalTable.addCell(new Cell().add(new Paragraph("TOTAL MAIN D'OEUVRE")).setBold());
            totalTable.addCell(new Cell().add(new Paragraph(formatCurrency(pf.getTotalMainOeuvre()))).setTextAlignment(TextAlignment.RIGHT));
            totalTable.addCell(new Cell().add(new Paragraph("DIAGNOSTIC" + (pf.getDiagnosticGratuit() ? " (GRATUIT)" : ""))).setBold());
            totalTable.addCell(new Cell().add(new Paragraph(formatCurrency(pf.getFraisDiagnostic()))).setTextAlignment(TextAlignment.RIGHT));
            if (pf.getFraisRecuperation() != null && pf.getFraisRecuperation().compareTo(BigDecimal.ZERO) > 0) {
                totalTable.addCell(new Cell().add(new Paragraph("FRAIS RECUPERATION")).setBold());
                totalTable.addCell(new Cell().add(new Paragraph(formatCurrency(pf.getFraisRecuperation()))).setTextAlignment(TextAlignment.RIGHT));
            }
            totalTable.addCell(new Cell().add(new Paragraph("TOTAL GÉNÉRAL")).setBold().setFontSize(14).setBackgroundColor(ColorConstants.YELLOW));
            totalTable.addCell(new Cell().add(new Paragraph(formatCurrency(pf.getTotalGeneral()))).setBold().setFontSize(14).setBackgroundColor(ColorConstants.YELLOW).setTextAlignment(TextAlignment.RIGHT));
            document.add(totalTable);
        }

        document.close();
        return baos.toByteArray();
    }

    // ─── FACTURE FINALE ──────────────────────────────────────────────────────

    public byte[] genererFactureCloture(DemandeIntervention demande) {
        ProForma pf = demande.getProForma();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(baos));
        Document doc = new Document(pdf);
        doc.setMargins(36, 50, 36, 50);

        DeviceRgb rouge       = new DeviceRgb(183, 28, 28);   // #b71c1c
        DeviceRgb gris        = new DeviceRgb(248, 250, 252);  // #f8fafc
        DeviceRgb grisBord    = new DeviceRgb(226, 232, 240);  // #e2e8f0
        DeviceRgb texte       = new DeviceRgb(30, 41, 59);     // #1e293b
        DeviceRgb texteLight  = new DeviceRgb(100, 116, 139);  // #64748b

        String refFacture = "FACT-" + (pf != null ? pf.getReference() : demande.getReference());
        String dateFacture = java.time.LocalDate.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // ── EN-TÊTE : logo + titre ────────────────────────────────────────────
        Table header = new Table(UnitValue.createPercentArray(new float[]{30, 70})).useAllAvailableWidth();
        header.setBorder(Border.NO_BORDER);

        // Logo
        Cell logoCell = new Cell().setBorder(Border.NO_BORDER).setVerticalAlignment(VerticalAlignment.MIDDLE);
        try {
            InputStream logoStream = getClass().getClassLoader()
                .getResourceAsStream("static/images/logo-diwa.png");
            if (logoStream != null) {
                Image logo = new Image(ImageDataFactory.create(logoStream.readAllBytes()));
                logo.setWidth(UnitValue.createPointValue(110));
                logoCell.add(logo);
            } else {
                logoCell.add(new Paragraph("DIWA").setBold().setFontSize(22).setFontColor(rouge));
            }
        } catch (Exception e) {
            logoCell.add(new Paragraph("DIWA").setBold().setFontSize(22).setFontColor(rouge));
        }
        header.addCell(logoCell);

        // Titre + infos
        Cell titreCell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT);
        titreCell.add(new Paragraph("FACTURE").setBold().setFontSize(28).setFontColor(rouge));
        titreCell.add(new Paragraph("Réf. : " + refFacture).setFontSize(10).setFontColor(texteLight));
        titreCell.add(new Paragraph("Date : " + dateFacture).setFontSize(10).setFontColor(texteLight));
        titreCell.add(new Paragraph("DIWA Internationale – Lomé, Togo").setFontSize(9).setFontColor(texteLight));
        header.addCell(titreCell);

        doc.add(header);
        doc.add(separateur(rouge));

        // ── INFOS CLIENT / VÉHICULE ───────────────────────────────────────────
        Table infoBlock = new Table(UnitValue.createPercentArray(new float[]{50, 50})).useAllAvailableWidth();
        infoBlock.setBorder(Border.NO_BORDER);

        // Client
        String clientNom = demande.getClient() != null
            ? (demande.getClient().getPrenom() + " " + demande.getClient().getNom()).trim()
            : "Client DIWA";
        String clientEmail = demande.getClient() != null && demande.getClient().getEmail() != null
            ? demande.getClient().getEmail() : "";

        Cell clientCell = new Cell().setBorder(Border.NO_BORDER)
            .setBackgroundColor(gris).setPadding(12)
            .add(new Paragraph("CLIENT").setBold().setFontSize(9).setFontColor(texteLight))
            .add(new Paragraph(clientNom).setBold().setFontSize(12).setFontColor(texte))
            .add(new Paragraph(clientEmail).setFontSize(9).setFontColor(texteLight));
        infoBlock.addCell(clientCell);

        // Véhicule
        String vehicule = ((demande.getVehiculeMarque() != null ? demande.getVehiculeMarque() : "") + " "
            + (demande.getVehiculeModele() != null ? demande.getVehiculeModele() : "")).trim();
        String immat = demande.getVehiculeImmatriculation() != null ? demande.getVehiculeImmatriculation() : "";
        String km = demande.getVehiculeKilometrage() != null ? demande.getVehiculeKilometrage() + " km" : "";
        String refDemande = demande.getReference() != null ? demande.getReference() : "";

        Cell veloCell = new Cell().setBorder(Border.NO_BORDER)
            .setBackgroundColor(gris).setPadding(12)
            .add(new Paragraph("VÉHICULE").setBold().setFontSize(9).setFontColor(texteLight))
            .add(new Paragraph(vehicule).setBold().setFontSize(12).setFontColor(texte))
            .add(new Paragraph("Immat. : " + immat + (km.isEmpty() ? "" : "   •   " + km))
                .setFontSize(9).setFontColor(texteLight))
            .add(new Paragraph("Dossier : " + refDemande).setFontSize(9).setFontColor(texteLight));
        infoBlock.addCell(veloCell);
        doc.add(infoBlock);
        doc.add(new Paragraph("\n").setFontSize(6));

        // ── TABLEAU PIÈCES ────────────────────────────────────────────────────
        if (pf != null && pf.getLignesTravaux() != null) {
            List<LigneProFormaTravail> lignes = pf.getLignesTravaux().stream()
                .filter(l -> Boolean.TRUE.equals(l.getCocheeParClient()))
                .toList();

            if (!lignes.isEmpty()) {
                doc.add(sectionTitle("PIÈCES DÉTACHÉES", rouge));
                Table t = tableBase(new float[]{45, 10, 22, 23});
                t.addHeaderCell(th("Désignation", texteLight));
                t.addHeaderCell(th("Qté", texteLight).setTextAlignment(TextAlignment.CENTER));
                t.addHeaderCell(th("Prix unitaire", texteLight).setTextAlignment(TextAlignment.RIGHT));
                t.addHeaderCell(th("Total", texteLight).setTextAlignment(TextAlignment.RIGHT));

                for (LigneProFormaTravail l : lignes) {
                    t.addCell(td(l.getDesignation(), texte));
                    t.addCell(td(String.valueOf(l.getQuantite().stripTrailingZeros().toPlainString()), texte)
                        .setTextAlignment(TextAlignment.CENTER));
                    t.addCell(td(formatCurrency(l.getPrixUnitaire()), texte).setTextAlignment(TextAlignment.RIGHT));
                    t.addCell(td(formatCurrency(l.getPrixTotal()), texte).setTextAlignment(TextAlignment.RIGHT));
                }
                doc.add(t);
                doc.add(new Paragraph("\n").setFontSize(4));
            }
        }

        // ── TABLEAU MAIN D'OEUVRE ─────────────────────────────────────────────
        if (pf != null && pf.getLignesMainOeuvre() != null) {
            List<LigneProFormaMainOeuvre> lignesMO = pf.getLignesMainOeuvre().stream()
                .filter(l -> Boolean.TRUE.equals(l.getCocheeParClient()))
                .toList();

            if (!lignesMO.isEmpty()) {
                doc.add(sectionTitle("MAIN D'ŒUVRE", rouge));
                Table t = tableBase(new float[]{45, 12, 22, 21});
                t.addHeaderCell(th("Intervention", texteLight));
                t.addHeaderCell(th("Heures", texteLight).setTextAlignment(TextAlignment.CENTER));
                t.addHeaderCell(th("Taux horaire", texteLight).setTextAlignment(TextAlignment.RIGHT));
                t.addHeaderCell(th("Total", texteLight).setTextAlignment(TextAlignment.RIGHT));

                for (LigneProFormaMainOeuvre l : lignesMO) {
                    t.addCell(td(l.getTypeIntervention(), texte));
                    t.addCell(td(l.getHeures().stripTrailingZeros().toPlainString() + " h", texte)
                        .setTextAlignment(TextAlignment.CENTER));
                    t.addCell(td(formatCurrency(l.getTauxHoraire()), texte).setTextAlignment(TextAlignment.RIGHT));
                    t.addCell(td(formatCurrency(l.getTotal()), texte).setTextAlignment(TextAlignment.RIGHT));
                }
                doc.add(t);
                doc.add(new Paragraph("\n").setFontSize(4));
            }
        }

        // ── RÉCAPITULATIF FINANCIER ───────────────────────────────────────────
        if (pf != null) {
            doc.add(sectionTitle("RÉCAPITULATIF", rouge));

            Table totaux = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                .useAllAvailableWidth();
            totaux.setBorder(Border.NO_BORDER);

            // Lignes de coûts
            totaux.addCell(ligneTotal("Pièces détachées", texteLight, false, false));
            totaux.addCell(ligneTotal(formatCurrency(pf.getTotalPieces()), texteLight, false, true));

            totaux.addCell(ligneTotal("Main d'œuvre", texteLight, false, false));
            totaux.addCell(ligneTotal(formatCurrency(pf.getTotalMainOeuvre()), texteLight, false, true));

            BigDecimal diagFrais = pf.getFraisDiagnostic() != null ? pf.getFraisDiagnostic() : BigDecimal.ZERO;
            String diagLabel = "Diagnostic" + (Boolean.TRUE.equals(pf.getDiagnosticGratuit()) ? " (OFFERT)" : "");
            totaux.addCell(ligneTotal(diagLabel, texteLight, false, false));
            totaux.addCell(ligneTotal(Boolean.TRUE.equals(pf.getDiagnosticGratuit()) ? "0 FCFA" : formatCurrency(diagFrais), texteLight, false, true));

            if (pf.getFraisRecuperation() != null && pf.getFraisRecuperation().compareTo(BigDecimal.ZERO) > 0) {
                totaux.addCell(ligneTotal("Frais de récupération", texteLight, false, false));
                totaux.addCell(ligneTotal(formatCurrency(pf.getFraisRecuperation()), texteLight, false, true));
            }

            if (pf.getFraisLivraison() != null && pf.getFraisLivraison().compareTo(BigDecimal.ZERO) > 0) {
                totaux.addCell(ligneTotal("Frais de livraison", texteLight, false, false));
                totaux.addCell(ligneTotal(formatCurrency(pf.getFraisLivraison()), texteLight, false, true));
            }

            if (pf.getMontantRemise() != null && pf.getMontantRemise().compareTo(BigDecimal.ZERO) > 0) {
                String couponLabel = "Remise" + (pf.getCouponCode() != null ? " (code : " + pf.getCouponCode() + ")" : "");
                totaux.addCell(ligneTotal(couponLabel, texteLight, false, false));
                totaux.addCell(ligneTotal("- " + formatCurrency(pf.getMontantRemise()), texteLight, false, true));
            }

            // Séparateur
            totaux.addCell(new Cell(1, 2).setBorderTop(new SolidBorder(rouge, 1.5f)).setBorderBottom(Border.NO_BORDER)
                .setBorderLeft(Border.NO_BORDER).setBorderRight(Border.NO_BORDER).setHeight(6));

            // TOTAL GÉNÉRAL
            BigDecimal totalFinal = pf.getTotalApresRemise();
            totaux.addCell(ligneTotal("TOTAL GÉNÉRAL", texte, true, false)
                .setFontSize(13).setBackgroundColor(gris));
            totaux.addCell(ligneTotal(formatCurrency(totalFinal), rouge, true, true)
                .setFontSize(13).setBackgroundColor(gris));

            doc.add(totaux);
        }

        // ── PIED DE PAGE ──────────────────────────────────────────────────────
        doc.add(separateur(grisBord));
        doc.add(new Paragraph("Merci pour votre confiance. DIWA Internationale — Lomé, Togo")
            .setFontSize(9).setFontColor(texteLight).setTextAlignment(TextAlignment.CENTER));
        doc.add(new Paragraph("Ce document tient lieu de facture officielle. Réf. dossier : " + demande.getReference())
            .setFontSize(8).setFontColor(texteLight).setTextAlignment(TextAlignment.CENTER));

        doc.close();
        return baos.toByteArray();
    }

    // ─── Helpers privés ──────────────────────────────────────────────────────

    private Paragraph separateur(DeviceRgb color) {
        return new Paragraph("").setMarginTop(6).setMarginBottom(6)
            .setBorderBottom(new SolidBorder(color, 1f));
    }

    private Paragraph sectionTitle(String text, DeviceRgb color) {
        return new Paragraph(text).setBold().setFontSize(10).setFontColor(color)
            .setMarginTop(8).setMarginBottom(4);
    }

    private Table tableBase(float[] widths) {
        Table t = new Table(UnitValue.createPercentArray(widths)).useAllAvailableWidth();
        return t;
    }

    private Cell th(String text, DeviceRgb color) {
        return new Cell()
            .add(new Paragraph(text).setFontSize(9).setBold().setFontColor(color))
            .setBackgroundColor(new DeviceRgb(241, 245, 249))
            .setBorder(Border.NO_BORDER)
            .setBorderBottom(new SolidBorder(new DeviceRgb(226, 232, 240), 1f))
            .setPadding(5);
    }

    private Cell td(String text, DeviceRgb color) {
        return new Cell()
            .add(new Paragraph(text != null ? text : "").setFontSize(9).setFontColor(color))
            .setBorder(Border.NO_BORDER)
            .setBorderBottom(new SolidBorder(new DeviceRgb(241, 245, 249), 0.5f))
            .setPadding(5);
    }

    private Cell ligneTotal(String text, DeviceRgb color, boolean bold, boolean right) {
        Paragraph p = new Paragraph(text).setFontSize(10).setFontColor(color);
        if (bold) p.setBold();
        Cell c = new Cell().add(p).setBorder(Border.NO_BORDER).setPaddingTop(4).setPaddingBottom(4);
        if (right) c.setTextAlignment(TextAlignment.RIGHT);
        return c;
    }

    public byte[] genererExcel(ProForma pf) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Pro Forma");
            
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("Référence :");
            row.createCell(1).setCellValue(pf.getReference());

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 FCFA";
        return currencyFormat.format(amount).replace("XOF", "FCFA");
    }
}
