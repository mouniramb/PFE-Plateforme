import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { PresenceService } from '../../services/presence.service';
import { Presence } from '../../models/planning.model';

@Component({
  selector: 'app-mes-presences-apprenant',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe],
  templateUrl: './mes-presences-apprenant.component.html',
  styleUrl: './mes-presences-apprenant.component.scss'
})
export class MesPresencesApprenantComponent implements OnInit {
  presencesByFormation: Map<string, Presence[]> = new Map();
  loading = true;
  expandedFormations: Set<string> = new Set();

  constructor(private presenceService: PresenceService) {}

  ngOnInit(): void {
    this.loadPresences();
  }

  loadPresences(): void {
    this.presenceService.getMesPresences().subscribe({
      next: (presences: Presence[]) => {
        this.groupPresencesByFormation(presences);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  groupPresencesByFormation(presences: Presence[]): void {
    const map = new Map<string, Presence[]>();
    presences.forEach(p => {
      const key = p.seanceTitre;
      if (!map.has(key)) {
        map.set(key, []);
      }
      map.get(key)?.push(p);
    });
    this.presencesByFormation = map;
  }

  toggleFormation(formation: string): void {
    if (this.expandedFormations.has(formation)) {
      this.expandedFormations.delete(formation);
    } else {
      this.expandedFormations.add(formation);
    }
  }

  isExpanded(formation: string): boolean {
    return this.expandedFormations.has(formation);
  }

  getTauxPresence(presences: Presence[]): number {
    if (presences.length === 0) return 0;
    const presentCount = presences.filter(p => p.statut === 'PRESENT').length;
    return presentCount / presences.length;
  }

  getCompteurs(presences: Presence[]): any {
    return {
      present: presences.filter(p => p.statut === 'PRESENT').length,
      absent: presences.filter(p => p.statut === 'ABSENT').length,
      retard: presences.filter(p => p.statut === 'RETARD').length,
      excuse: presences.filter(p => p.statut === 'EXCUSE').length
    };
  }

  getTauxColor(taux: number): string {
    if (taux >= 0.8) return 'taux-vert';
    if (taux >= 0.6) return 'taux-orange';
    return 'taux-rouge';
  }

  getStatutColor(statut: string): string {
    switch (statut) {
      case 'PRESENT': return 'statut-present';
      case 'ABSENT': return 'statut-absent';
      case 'RETARD': return 'statut-retard';
      case 'EXCUSE': return 'statut-excuse';
      default: return '';
    }
  }
}
