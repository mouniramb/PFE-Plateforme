import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { SuiviService } from '../../services/suivi.service';
import { StatistiquesApprenant } from '../../models/planning.model';

@Component({
  selector: 'app-admin-detail-apprenant',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './admin-detail-apprenant.component.html',
  styleUrls: ['./admin-detail-apprenant.component.scss']
})
export class AdminDetailApprenantComponent implements OnInit {

  data        : StatistiquesApprenant | null = null;
  isLoading   = false;
  errorMessage = '';
  formationId = 0;
  apprenantId = 0;

  constructor(
    private route  : ActivatedRoute,
    private router : Router,
    private suivi  : SuiviService
  ) {}

  ngOnInit(): void {
    this.formationId = +this.route.snapshot.paramMap.get('formationId')!;
    this.apprenantId = +this.route.snapshot.paramMap.get('apprenantId')!;
    this.load();
  }

  load(): void {
    this.isLoading    = true;
    this.errorMessage = '';
    this.suivi.getDetailApprenant(this.formationId, this.apprenantId).subscribe({
      next : (d) => { this.data = d; this.isLoading = false; },
      error: ()  => {
        this.errorMessage = 'Impossible de charger les données de cet apprenant.';
        this.isLoading = false;
      }
    });
  }

  retour(): void {
    this.router.navigate(['/admin/formations', this.formationId, 'suivi']);
  }

  // ── Initiales avatar ────────────────────────────────────────────────
  get initiales(): string {
    if (!this.data) return '??';
    return (this.data.prenom.charAt(0) + this.data.nom.charAt(0)).toUpperCase();
  }

  // ── Couleur moyenne ─────────────────────────────────────────────────
  getMoyenneClass(val: number): string {
    if (val >= 15) return 'excellent';
    if (val >= 12) return 'bon';
    if (val >= 10) return 'moyen';
    return 'faible';
  }

  getMoyenneLabel(val: number): string {
    if (val >= 15) return 'Excellent';
    if (val >= 12) return 'Bon';
    if (val >= 10) return 'Moyen';
    return 'Insuffisant';
  }

  // ── Couleur taux présence ───────────────────────────────────────────
  getTauxClass(taux: number): string {
    const pct = taux * 100;
    if (pct >= 80) return 'excellent';
    if (pct >= 60) return 'bon';
    if (pct >= 40) return 'moyen';
    return 'faible';
  }

  // ── Couleur badge présence ──────────────────────────────────────────
  getPresenceClass(statut: string): string {
    const map: Record<string, string> = {
      PRESENT: 'badge-present',
      ABSENT : 'badge-absent',
      RETARD : 'badge-retard',
      EXCUSE : 'badge-excuse'
    };
    return map[statut] ?? '';
  }

  getPresenceLabel(statut: string): string {
    const map: Record<string, string> = {
      PRESENT: 'Présent',
      ABSENT : 'Absent',
      RETARD : 'Retard',
      EXCUSE : 'Excusé'
    };
    return map[statut] ?? statut;
  }

  // ── Format type évaluation ──────────────────────────────────────────
  getTypeLabel(type: string): string {
    const map: Record<string, string> = {
      EXAMEN  : 'Examen',
      DEVOIR  : 'Devoir',
      QUIZ    : 'Quiz',
      PROJET  : 'Projet',
      CONTROLE: 'Contrôle'
    };
    return map[type] ?? type;
  }

  formatDate(dateStr: string): string {
    if (!dateStr) return '—';
    return new Date(dateStr).toLocaleDateString('fr-FR', {
      day: '2-digit', month: 'short', year: 'numeric'
    });
  }
}
