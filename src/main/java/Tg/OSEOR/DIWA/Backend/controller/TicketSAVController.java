package Tg.OSEOR.DIWA.Backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import Tg.OSEOR.DIWA.Backend.dto.TicketSAVDTO.TicketSAVDTOResponse;
import Tg.OSEOR.DIWA.Backend.entity.CommentaireTicket;
import Tg.OSEOR.DIWA.Backend.entity.TicketSAV;
import Tg.OSEOR.DIWA.Backend.mappers.TicketSAVMapper;
import Tg.OSEOR.DIWA.Backend.service.TicketSAVService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sav")
@Tag(name = "SAV Tickets", description = "Module de gestion des tickets de service après-vente")
public class TicketSAVController {

    @Autowired
    private TicketSAVService ticketService;

    @Autowired
    private TicketSAVMapper ticketMapper;

    @PostMapping("/tickets")
    @Operation(summary = "Créer un ticket SAV (CLIENT)")
    public ResponseEntity<TicketSAVDTOResponse> createTicket(
            @RequestParam String vehiculeImmat,
            @RequestParam String vehiculeMarque,
            @RequestParam String description,
            @RequestParam Long userId) {
        TicketSAV ticket = ticketService.create(vehiculeImmat, vehiculeMarque, description, userId);
        return ResponseEntity.status(201).body(ticketMapper.toResponse(ticket));
    }

    @GetMapping("/tickets/mes")
    @Operation(summary = "Obtenir mes tickets SAV")
    public ResponseEntity<List<TicketSAVDTOResponse>> getMesTickets(@RequestParam Long userId) {
        List<TicketSAVDTOResponse> dtos = ticketService.getByUser(userId).stream()
            .map(ticketMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/tickets")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TECHNICIEN') or hasRole('CHEF_TECHNICIEN') or hasRole('RECEPTIONNISTE')")
    @Operation(summary = "Obtenir tous les tickets (ADMIN/TECH/CHEF)")
    public ResponseEntity<List<TicketSAVDTOResponse>> getAllTickets() {
        List<TicketSAVDTOResponse> dtos = ticketService.getAll().stream()
            .map(ticketMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/tickets/{id}/statut")
    @PreAuthorize("hasRole('TECHNICIEN') or hasRole('ADMIN') or hasRole('CHEF_TECHNICIEN')")
    @Operation(summary = "Mettre à jour le statut d'un ticket (TECHNICIEN/ADMIN)")
    public ResponseEntity<TicketSAVDTOResponse> updateStatut(
            @PathVariable Long id,
            @RequestParam String statut) {
        return ResponseEntity.ok(ticketMapper.toResponse(ticketService.updateStatut(id, statut)));
    }

    @PutMapping("/tickets/{id}/technicien")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assigner un technicien à un ticket (ADMIN uniquement)")
    public ResponseEntity<TicketSAVDTOResponse> assignTechnicien(
            @PathVariable Long id,
            @RequestParam Long technicienId) {
        return ResponseEntity.ok(ticketMapper.toResponse(ticketService.assignTechnicien(id, technicienId)));
    }

    @PostMapping("/tickets/{id}/commentaires")
    @Operation(summary = "Ajouter un commentaire à un ticket SAV")
    public ResponseEntity<TicketSAVDTOResponse.CommentaireTicketDTO> addCommentaire(
            @PathVariable Long id,
            @RequestParam String auteur,
            @RequestParam String message) {
        CommentaireTicket c = ticketService.addCommentaire(id, auteur, message);
        return ResponseEntity.status(201).body(new TicketSAVDTOResponse.CommentaireTicketDTO(
            c.getMessage(), c.getAuteur(), c.getDateCommentaire()));
    }
}
