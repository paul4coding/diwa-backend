package Tg.OSEOR.DIWA.Backend.service;

import Tg.OSEOR.DIWA.Backend.entity.RendezVous;
import Tg.OSEOR.DIWA.Backend.entity.DemandeIntervention;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service("savEmailService")
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    // ── MÉTHODES SÉCURITÉ / AUTH ─────────────────────────────

    @Async
    public void sendOtpEmail(String toEmail, String userName, String otpCode) {
        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("otpCode", otpCode);
        context.setVariable("expirationMinutes", 10);

        String htmlContent = templateEngine.process("email/otp-login", context);
        sendHtmlEmail(toEmail, "🔐 Votre code de connexion DIWA — " + otpCode, htmlContent);
    }

    @Async
    public void sendAccountConfirmationEmail(String toEmail, String userName, String confirmationToken) {
        String confirmationUrl = "http://localhost:8181/api/auth/confirm?token=" + confirmationToken;

        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("confirmationUrl", confirmationUrl);
        context.setVariable("expirationHours", 24);

        String htmlContent = templateEngine.process("email/account-confirmation", context);
        sendHtmlEmail(toEmail, "✅ Confirmez votre compte DIWA Internationale", htmlContent);
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String userName, String resetCode) {
        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("resetCode", resetCode);
        context.setVariable("expirationMinutes", 10);

        String htmlContent = templateEngine.process("email/password-reset", context);
        sendHtmlEmail(toEmail, "🔑 Réinitialisation de votre mot de passe DIWA", htmlContent);
    }

    @Async
    public void sendAdminWelcomeEmail(String toEmail, String userName, String tempPassword) {
        Context context = new Context();
        context.setVariable("userName", userName);
        context.setVariable("tempPassword", tempPassword);
        context.setVariable("loginUrl", frontendUrl + "/login");

        String htmlContent = templateEngine.process("email/admin-welcome", context);
        sendHtmlEmail(toEmail, "👋 Bienvenue dans l'admin DIWA Internationale", htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("ERREUR envoi email à " + to + " : " + e.getMessage());
        }
    }

    // ── MÉTHODES SAV / ATELIER ──────────────────────────────

    public void sendRendezVousConfirmation(RendezVous rdv) {
        if (rdv == null || rdv.getUser() == null || rdv.getUser().getEmail() == null) return;
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(rdv.getUser().getEmail());
            helper.setSubject("Confirmation de votre Rendez-vous SAV - DIWA Internationale");

            String clientName = rdv.getUser().getPrenom() != null ? rdv.getUser().getPrenom() : rdv.getUser().getUsername();
            String dateRdv = rdv.getDate() != null ? rdv.getDate().format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy")) : "À confirmer";
            String heureRdv = "À confirmer";
            if (rdv.getCreneau() != null && rdv.getCreneau().getHeureDebut() != null) {
                heureRdv = rdv.getCreneau().getHeureDebut().toString();
            } else if (rdv.getHeureDebut() != null) {
                heureRdv = rdv.getHeureDebut();
            }
            
            String motif = (rdv.getService() != null) ? rdv.getService().getLibelle() : "Intervention SAV";
            String vehicule = (rdv.getModeleVehicule() != null) ? rdv.getModeleVehicule() : "Votre véhicule bénéficiaire";

            String content = "<html><body style='font-family: Arial, sans-serif; background-color: #f4f7f9; margin: 0; padding: 20px;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1);'>" +
                    "  <div style='background: #0f172a; padding: 30px; text-align: center; border-bottom: 4px solid #ef4444;'>" +
                    "    <h1 style='color: #ffffff; margin: 0; letter-spacing: 5px; font-size: 28px;'>DIWA ELITE</h1>" +
                    "    <p style='color: #94a3b8; font-size: 12px; margin-top: 5px;'>SERVICE APRÈS-VENTE D'EXCELLENCE</p>" +
                    "  </div>" +
                    "  <div style='padding: 40px; color: #1e293b; line-height: 1.6;'>" +
                    "    <h2 style='color: #0f172a; margin-top: 0;'>Confirmation de Prise en Charge</h2>" +
                    "    <p>Bonjour <strong>" + clientName + "</strong>,</p>" +
                    "    <p>Nous avons le plaisir de vous confirmer la planification de votre rendez-vous. Nos experts techniques sont prêts à accueillir votre véhicule.</p>" +
                    "    <div style='background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 20px; margin: 25px 0;'>" +
                    "      <table style='width: 100%; font-size: 14px;'>" +
                    "        <tr><td style='color: #64748b; padding-bottom: 5px;'>DATE DU RDV</td><td style='text-align: right; font-weight: bold; color: #0f172a;'>" + dateRdv + "</td></tr>" +
                    "        <tr><td style='color: #64748b; padding-bottom: 5px;'>HEURE</td><td style='text-align: right; font-weight: bold; color: #ef4444;'>" + heureRdv + "</td></tr>" +
                    "        <tr><td style='color: #64748b; padding-bottom: 5px;'>MOTIF</td><td style='text-align: right; font-weight: bold; color: #0f172a;'>" + motif + "</td></tr>" +
                    "        <tr><td style='color: #64748b;'>VÉHICULE</td><td style='text-align: right; font-weight: bold; color: #0f172a;'>" + vehicule + "</td></tr>" +
                    "      </table>" +
                    "    </div>" +
                    "    <p style='font-size: 13px; color: #64748b;'>Veuillez trouver ci-joint votre fiche de confirmation officielle au format PDF.</p>" +
                    "    <div style='margin-top: 40px; border-top: 1px solid #f1f5f9; padding-top: 20px;'>" +
                    "      <p style='margin: 0; font-weight: bold;'>DIWA Internationale</p>" +
                    "      <p style='margin: 0; font-size: 12px; color: #94a3b8;'>Lomé, Togo • Excellence & Performance</p>" +
                    "    </div>" +
                    "  </div>" +
                    "</div>" +
                    "</body></html>";

            helper.setText(content, true);

            try {
                byte[] pdfBytes = generateRendezVousPdf(rdv);
                helper.addAttachment("Confirmation_RDV_DIWA_" + rdv.getId() + ".pdf", new ByteArrayResource(pdfBytes));
            } catch (Exception e) {
                System.err.println("ALERTE PDF: " + e.getMessage());
            }

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("CRASH EMAIL SERVICE SAV: " + e.getMessage());
        }
    }

    public void sendRendezVousAnnulation(RendezVous rdv) {
        if (rdv == null || rdv.getUser() == null || rdv.getUser().getEmail() == null) return;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(rdv.getUser().getEmail());
            helper.setSubject("Annulation de votre Rendez-vous SAV - DIWA Internationale");

            String clientName = rdv.getUser().getPrenom() != null ? rdv.getUser().getPrenom() : rdv.getUser().getUsername();
            String dateRdv = rdv.getDate() != null ? rdv.getDate().format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy")) : "N/A";

            String content = "<html><body style='font-family: Arial, sans-serif; background-color: #fff5f5; margin: 0; padding: 20px;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 8px; overflow: hidden; border: 1px solid #feb2b2;'>" +
                    "  <div style='background: #c53030; padding: 25px; text-align: center;'>" +
                    "    <h1 style='color: #ffffff; margin: 0; font-size: 24px;'>ANNULATION RENDEZ-VOUS</h1>" +
                    "  </div>" +
                    "  <div style='padding: 40px; color: #2d3748; line-height: 1.6;'>" +
                    "    <p>Bonjour <strong>" + clientName + "</strong>,</p>" +
                    "    <p>Nous vous informons que votre rendez-vous prévu le <strong>" + dateRdv + "</strong> a été annulé par notre service client.</p>" +
                    "    <p>Nous vous prions de nous excuser pour ce désagrément. Nos équipes reprendront contact avec vous très prochainement pour convenir d'une nouvelle date.</p>" +
                    "    <div style='margin-top: 40px; border-top: 1px solid #edf2f7; padding-top: 20px;'>" +
                    "      <p style='margin: 0; font-weight: bold;'>DIWA Internationale</p>" +
                    "      <p style='margin: 0; font-size: 12px; color: #a0aec0;'>Service Après-Vente Elite</p>" +
                    "    </div>" +
                    "  </div>" +
                    "</div>" +
                    "</body></html>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERREUR ENVOI ANNULATION RDV: " + e.getMessage());
        }
    }

    private byte[] generateRendezVousPdf(RendezVous rdv) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);

        document.open();
        
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(0, 0, 0));
        Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(64, 64, 64));
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12, new Color(0, 0, 0));
        Font redFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(183, 28, 28));

        document.add(new Paragraph("CONFIRMATION DE RENDEZ-VOUS SAV", titleFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("DIWA INTERNATIONALE", subTitleFont));
        document.add(new Paragraph("Service Après-Vente de Luxe", bodyFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph("----------------------------------------------------------------------------------------------------------------------------------"));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Informations Client :", subTitleFont));
        String client = (rdv.getUser() != null) ? (rdv.getUser().getPrenom() + " " + rdv.getUser().getNom()) : "Client DIWA";
        document.add(new Paragraph("Nom : " + client, bodyFont));
        if (rdv.getUser() != null) document.add(new Paragraph("Email : " + rdv.getUser().getEmail(), bodyFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Détails de l'intervention :", subTitleFont));
        String motif = (rdv.getService() != null) ? rdv.getService().getLibelle() : "Entretien SAV";
        document.add(new Paragraph("Motif : " + motif, bodyFont));
        
        String veh = (rdv.getModeleVehicule() != null) ? rdv.getModeleVehicule() : "Véhicule";
        document.add(new Paragraph("Véhicule : " + veh + " / " + (rdv.getImmatriculation() != null ? rdv.getImmatriculation() : "---"), bodyFont));
        if (rdv.getVin() != null) document.add(new Paragraph("Châssis (VIN) : " + rdv.getVin(), bodyFont));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Planification :", subTitleFont));
        String dateS = (rdv.getDate() != null) ? rdv.getDate().format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy")) : "À CONFIRMER";
        document.add(new Paragraph("DATE : " + dateS, redFont));
        
        String hor = "HEURE À CONFIRMER";
        if (rdv.getCreneau() != null && rdv.getCreneau().getHeureDebut() != null) {
            hor = rdv.getCreneau().getHeureDebut().toString();
        } else if (rdv.getHeureDebut() != null) {
            hor = rdv.getHeureDebut();
        }
        
        document.add(new Paragraph("HORAIRE : " + hor, redFont));
        
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Instructions : Veuillez vous présenter 10 minutes avant l'heure prévue.", bodyFont));
        
        document.close();
        return out.toByteArray();
    }

    public void sendVehiculeReceptionne(String email, String prenom, String marque, String immatriculation) {
        System.out.println("LOG: Notification réception envoyée à " + email);
    }

    @Async
    public void sendReparationEnCours(DemandeIntervention d, LocalDateTime finPrevue) {
        if (d.getClient() == null || d.getClient().getEmail() == null) return;
        String dateS = finPrevue.format(DateTimeFormatter.ofPattern("EEEE dd MMMM 'à' HH'h'mm"));
        String content = "<html><body>" +
                "<h2>Intervention en cours 🛠️</h2>" +
                "<p>Bonjour " + d.getClient().getPrenom() + ",</p>" +
                "<p>Les travaux sur votre véhicule <strong>" + d.getVehiculeMarque() + " " + d.getVehiculeModele() + "</strong> ont débuté.</p>" +
                "<p>La fin des travaux est estimée au : <strong>" + dateS + "</strong>.</p>" +
                "<p>Vous serez notifié dès que le véhicule sera prêt.</p>" +
                "</body></html>";
        sendHtmlEmail(d.getClient().getEmail(), "🛠️ Travaux en cours sur votre véhicule — DIWA", content);
    }

    @Async
    public void sendPlanningUpdate(DemandeIntervention d, LocalDateTime nouvelleFin) {
        if (d.getClient() == null || d.getClient().getEmail() == null) return;
        String dateS = nouvelleFin.format(DateTimeFormatter.ofPattern("EEEE dd MMMM 'à' HH'h'mm"));
        String content = "<html><body>" +
                "<h2>Mise à jour du planning 📅</h2>" +
                "<p>Bonjour " + d.getClient().getPrenom() + ",</p>" +
                "<p>La planification des travaux sur votre véhicule <strong>" + d.getVehiculeMarque() + " " + d.getVehiculeModele() + "</strong> a été mise à jour.</p>" +
                "<p>La nouvelle date de fin estimée est : <strong>" + dateS + "</strong>.</p>" +
                "</body></html>";
        sendHtmlEmail(d.getClient().getEmail(), "📅 Mise à jour planning — " + d.getVehiculeMarque(), content);
    }

    @Async
    public void sendVehiculePret(DemandeIntervention d) {
        if (d.getClient() == null || d.getClient().getEmail() == null) return;
        String content = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background: #10b981; color: white; padding: 20px; text-align: center; border-radius: 10px;'>" +
                "  <h1 style='margin: 0;'>VOTRE VÉHICULE EST PRÊT ! ✨</h1>" +
                "</div>" +
                "<p>Bonjour <strong>" + d.getClient().getPrenom() + "</strong>,</p>" +
                "<p>Nous avons le plaisir de vous informer que les travaux sur votre <strong>" + d.getVehiculeMarque() + " " + d.getVehiculeModele() + "</strong> sont terminés.</p>" +
                "<p>Le véhicule a passé avec succès tous nos points de contrôle après intervention.</p>" +
                "<div style='background: #f8fafc; padding: 20px; border-radius: 10px; margin: 20px 0;'>" +
                "  <p style='margin: 0;'>📍 <strong>Lieu :</strong> DIWA Internationale - Atelier Principal</p>" +
                "  <p style='margin: 10px 0 0;'>⌚ <strong>Horaires :</strong> 08h00 - 18h00</p>" +
                "</div>" +
                "<p>À très bientôt dans nos locaux !</p>" +
                "</body></html>";
        sendHtmlEmail(d.getClient().getEmail(), "✨ Votre véhicule est prêt ! — DIWA Internationale", content);
    }

    public void sendVehiculePret(String email, String prenom, String marque, String immatriculation) {
        if (email == null) return;
        String content = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background: #10b981; color: white; padding: 20px; text-align: center; border-radius: 10px;'>" +
                "  <h1 style='margin: 0;'>VOTRE VÉHICULE EST PRÊT ! ✨</h1>" +
                "</div>" +
                "<p>Bonjour <strong>" + prenom + "</strong>,</p>" +
                "<p>Les travaux sur votre véhicule <strong>" + marque + " (" + immatriculation + ")</strong> sont terminés.</p>" +
                "<p>Vous pouvez passer le récupérer à nos locaux ou notre équipe vous contactera prochainement.</p>" +
                "</body></html>";
        sendHtmlEmail(email, "✨ Votre véhicule est prêt ! — DIWA Internationale", content);
    }

    @Async
    public void sendLivraisonFraisAjoutes(String email, String prenom, String marque, String immat,
                                           java.math.BigDecimal fraisLivraison, java.math.BigDecimal totalGeneral) {
        if (email == null) return;
        String content = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background: #3b82f6; color: white; padding: 20px; text-align: center; border-radius: 10px;'>" +
                "  <h1 style='margin: 0;'>Action requise — Confirmation de livraison</h1>" +
                "</div>" +
                "<p>Bonjour <strong>" + prenom + "</strong>,</p>" +
                "<p>Les frais de livraison à domicile pour votre véhicule <strong>" + marque + " (" + immat + ")</strong> ont été fixés :</p>" +
                "<ul>" +
                "<li><strong>Frais de livraison :</strong> " + fraisLivraison.toPlainString() + " FCFA</li>" +
                "<li><strong>Total final :</strong> " + (totalGeneral != null ? totalGeneral.toPlainString() : "voir détails") + " FCFA</li>" +
                "</ul>" +
                "<p>Veuillez vous connecter à votre espace client pour confirmer le devis final avant lancement des travaux.</p>" +
                "</body></html>";
        sendHtmlEmail(email, "📦 Frais de livraison ajoutés — Confirmation requise", content);
    }

    @Async
    public void sendLivraisonConfirmee(String email, String prenom, String marque, String immat) {
        if (email == null) return;
        String content = "<html><body style='font-family: Arial, sans-serif;'>" +
                "<div style='background: #10b981; color: white; padding: 20px; text-align: center; border-radius: 10px;'>" +
                "  <h1 style='margin: 0;'>Votre véhicule est livré ! 🚗</h1>" +
                "</div>" +
                "<p>Bonjour <strong>" + prenom + "</strong>,</p>" +
                "<p>Votre véhicule <strong>" + marque + " (" + immat + ")</strong> vous a été livré à domicile.</p>" +
                "<p>Merci pour votre confiance. À bientôt chez DIWA Internationale !</p>" +
                "</body></html>";
        sendHtmlEmail(email, "🚗 Votre véhicule vous a été livré — DIWA Internationale", content);
    }

    public void sendExpertVisitAssignment(DemandeIntervention demande) {
        if (demande == null || demande.getTechnicien() == null || demande.getTechnicien().getEmail() == null) return;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(demande.getTechnicien().getEmail());
            helper.setSubject("NOUVELLE MISSION : Visite Expert à domicile - " + demande.getReference());

            String techName = demande.getTechnicien().getPrenom() + " " + demande.getTechnicien().getNom();
            String clientName = demande.getClientNom();
            String adresse = demande.getAdresseVisite();
            String date = (demande.getCreneauVisite() != null) ? demande.getCreneauVisite().getDate().toString() : "N/A";
            String heure = (demande.getCreneauVisite() != null) ? (demande.getCreneauVisite().getHeureDebut() != null ? demande.getCreneauVisite().getHeureDebut().toString() : "N/A") : "N/A";

            String content = "<html><body style='font-family: Arial, sans-serif; background-color: #f8fafc; margin: 0; padding: 20px;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; border: 1px solid #e2e8f0;'>" +
                    "  <div style='background: #1e293b; padding: 25px; text-align: center;'>" +
                    "    <h2 style='color: #ffffff; margin: 0; font-size: 20px;'>NOUVELLE MISSION ASSIGNÉE</h2>" +
                    "  </div>" +
                    "  <div style='padding: 30px; color: #334155;'>" +
                    "    <p>Bonjour <strong>" + techName + "</strong>,</p>" +
                    "    <p>Une nouvelle demande de visite à domicile vous a été assignée par la réception.</p>" +
                    "    <div style='background: #f1f5f9; border-radius: 8px; padding: 20px; margin: 20px 0;'>" +
                    "      <p style='margin: 0 0 10px 0;'><strong>Client :</strong> " + clientName + "</p>" +
                    "      <p style='margin: 0 0 10px 0;'><strong>Adresse :</strong> " + adresse + "</p>" +
                    "      <p style='margin: 0 0 10px 0;'><strong>Rendez-vous :</strong> Le " + date + " à " + heure + "</p>" +
                    "      <p style='margin: 0;'><strong>Véhicule :</strong> " + demande.getVehiculeMarque() + " " + demande.getVehiculeModele() + "</p>" +
                    "    </div>" +
                    "    <p>Veuillez vous rendre sur votre tableau de bord technicien pour confirmer la récupération du véhicule une fois sur place.</p>" +
                    "    <div style='margin-top: 30px; padding-top: 20px; border-top: 1px solid #e2e8f0; font-size: 12px; color: #94a3b8;'>" +
                    "      DIWA Internationale - Système de Gestion de Flotte Elite" +
                    "    </div>" +
                    "  </div>" +
                    "</div>" +
                    "</body></html>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERREUR ENVOI EMAIL TECHNICIEN: " + e.getMessage());
        }
    }

    public void sendContactEmail(Tg.OSEOR.DIWA.Backend.dto.ContactRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(fromEmail);
            helper.setReplyTo(request.getEmail());
            helper.setSubject("Nouveau message de contact : " + request.getNom());

            String telephone = (request.getTelephone() != null && !request.getTelephone().isEmpty()) ? request.getTelephone() : "Non renseigné";

            String content = "<html><body style='font-family: Arial, sans-serif; background-color: #f4f7f9; margin: 0; padding: 20px;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1);'>" +
                    "  <div style='background: #0f172a; padding: 20px; text-align: center; border-bottom: 4px solid #ef4444;'>" +
                    "    <h2 style='color: #ffffff; margin: 0;'>Nouveau Message de Contact</h2>" +
                    "  </div>" +
                    "  <div style='padding: 30px; color: #1e293b; line-height: 1.6;'>" +
                    "    <p><strong>Nom :</strong> " + request.getNom() + "</p>" +
                    "    <p><strong>Email :</strong> " + request.getEmail() + "</p>" +
                    "    <p><strong>Téléphone :</strong> " + telephone + "</p>" +
                    "    <hr style='border: 0; border-top: 1px solid #e2e8f0; margin: 20px 0;' />" +
                    "    <p><strong>Message :</strong></p>" +
                    "    <p style='background: #f8fafc; padding: 15px; border-radius: 5px; font-style: italic; white-space: pre-wrap;'>" + request.getMessage().replace("<", "&lt;").replace(">", "&gt;") + "</p>" +
                    "  </div>" +
                    "</div>" +
                    "</body></html>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERREUR ENVOI CONTACT EMAIL: " + e.getMessage());
        }
    }

    public void sendContactReply(String clientEmail, String clientNom, String messageOriginal, String replyContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(clientEmail);
            helper.setSubject("Réponse à votre message - DIWA Internationale");

            String content = "<html><body style='font-family: Arial, sans-serif; color: #334155; line-height: 1.6;'>" +
                    "<div style='max-width: 600px; margin: 0 auto; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden;'>" +
                    "  <div style='background: #0f172a; padding: 25px; text-align: center;'>" +
                    "    <h2 style='color: #ffffff; margin: 0;'>DIWA INTERNATIONALE</h2>" +
                    "  </div>" +
                    "  <div style='padding: 30px;'>" +
                    "    <p>Bonjour <strong>" + clientNom + "</strong>,</p>" +
                    "    <p>Nous vous remercions de nous avoir contactés. Voici notre réponse à votre message :</p>" +
                    "    <div style='background: #f1f5f9; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #ef4444;'>" +
                    "      " + replyContent.replace("\n", "<br/>") + " " +
                    "    </div>" +
                    "    <div style='font-size: 13px; color: #64748b; margin-top: 30px;'>" +
                    "      <p><em>Rappel de votre message :</em></p>" +
                    "      <blockquote style='border-left: 2px solid #cbd5e1; padding-left: 15px; font-style: italic;'>" +
                    "        " + messageOriginal.replace("\n", "<br/>") + " " +
                    "      </blockquote>" +
                    "    </div>" +
                    "    <p style='margin-top: 30px;'>Cordialement,<br/><strong>L'équipe DIWA Internationale</strong></p>" +
                    "  </div>" +
                    "</div>" +
                    "</body></html>";

            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("ERREUR ENVOI REPONSE CONTACT: " + e.getMessage());
        }
    }

    // ── FACTURE FINALE (Sprint 6) ────────────────────────────────────────────

    @Async
    public void sendFactureCloture(String email, String prenom, String marque,
                                   String immat, String refDossier, byte[] pdfBytes) {
        if (email == null) return;
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(email);
            helper.setSubject("🧾 Votre facture DIWA — Dossier " + refDossier);

            String vehiculeLabel = (marque != null ? marque : "votre véhicule")
                + (immat != null ? " (" + immat + ")" : "");

            String content = "<html><body style='font-family: Arial, sans-serif; background-color: #f8fafc; margin: 0; padding: 20px;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.08);'>" +
                "  <div style='background: #1e293b; padding: 28px 30px; border-bottom: 4px solid #b71c1c;'>" +
                "    <h1 style='color: #ffffff; margin: 0; font-size: 22px; letter-spacing: 3px;'>DIWA INTERNATIONALE</h1>" +
                "    <p style='color: #94a3b8; font-size: 11px; margin: 5px 0 0;'>FACTURE FINALE – DOSSIER CLÔTURÉ</p>" +
                "  </div>" +
                "  <div style='padding: 36px 30px; color: #1e293b; line-height: 1.7;'>" +
                "    <p>Bonjour <strong>" + prenom + "</strong>,</p>" +
                "    <p>Votre dossier <strong>" + refDossier + "</strong> concernant " + vehiculeLabel +
                       " a été clôturé avec succès.</p>" +
                "    <p>Veuillez trouver ci-joint la <strong>facture officielle</strong> de votre intervention." +
                       " Elle est également disponible à tout moment depuis votre espace client.</p>" +
                "    <div style='background: #f1f5f9; border-radius: 8px; padding: 16px 20px; margin: 24px 0; border-left: 4px solid #b71c1c;'>" +
                "      <p style='margin: 0; font-size: 13px; color: #64748b;'>Référence dossier</p>" +
                "      <p style='margin: 4px 0 0; font-size: 16px; font-weight: bold;'>" + refDossier + "</p>" +
                "    </div>" +
                "    <p style='font-size: 13px; color: #64748b;'>Merci de votre confiance. Nous espérons vous revoir bientôt chez DIWA Internationale.</p>" +
                "    <div style='margin-top: 36px; border-top: 1px solid #e2e8f0; padding-top: 18px;'>" +
                "      <p style='margin: 0; font-weight: bold; font-size: 13px;'>DIWA Internationale</p>" +
                "      <p style='margin: 2px 0 0; font-size: 11px; color: #94a3b8;'>Lomé, Togo • Excellence & Performance</p>" +
                "    </div>" +
                "  </div>" +
                "</div>" +
                "</body></html>";

            helper.setText(content, true);

            // Pièce jointe PDF
            if (pdfBytes != null && pdfBytes.length > 0) {
                String attachName = "Facture_DIWA_" + refDossier.replaceAll("[^a-zA-Z0-9_-]", "_") + ".pdf";
                helper.addAttachment(attachName, new ByteArrayResource(pdfBytes));
            }

            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("[DIWA] Erreur envoi email facture : " + e.getMessage());
        }
    }
}
