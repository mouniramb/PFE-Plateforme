import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { SeanceService } from '../../services/seance.service';
import { Seance, PlanningDTO } from '../../models/planning.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-planning-calendrier',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './planning-calendrier.component.html',
  styleUrl: './planning-calendrier.component.scss'
})
export class PlanningCalendrierComponent implements OnInit {
  seances: Seance[] = [];
  weekStart!: Date;
  weekEnd!: Date;
  currentUser: any;
  userRole: string = '';
  currentUserId: number | null = null;

  selectedSeanceDetail: Seance | null = null;
  showDetailPanel = false;

  heures = Array.from({ length: 13 }, (_, i) => 8 + i);
  jours = ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam'];

  activeMenuId = 'planning';

  constructor(
    private seanceService: SeanceService,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.userRole = this.authService.getUserRole() || '';
    this.currentUserId = this.authService.getUserId();
    this.setCurrentWeek();
    this.loadPlanning();
  }

  setCurrentWeek(): void {
    const today = new Date();
    const day = today.getDay();
    const diff = today.getDate() - day + (day === 0 ? -6 : 1);
    this.weekStart = new Date(today.setDate(diff));
    this.weekEnd = new Date(this.weekStart);
    this.weekEnd.setDate(this.weekEnd.getDate() + 6);
  }

  previousWeek(): void {
    this.weekStart.setDate(this.weekStart.getDate() - 7);
    this.weekEnd.setDate(this.weekEnd.getDate() - 7);
    this.loadPlanning();
  }

  nextWeek(): void {
    this.weekStart.setDate(this.weekStart.getDate() + 7);
    this.weekEnd.setDate(this.weekEnd.getDate() + 7);
    this.loadPlanning();
  }

  loadPlanning(): void {
    const debut = this.weekStart.toISOString().split('T')[0];
    const fin = this.weekEnd.toISOString().split('T')[0];

    let planningCall;
    if (this.userRole === 'ADMIN') {
      planningCall = this.seanceService.getPlanningAdmin(debut, fin);
    } else if (this.userRole === 'FORMATEUR') {
      planningCall = this.seanceService.getPlanningFormateur(this.currentUser.id, debut, fin);
    } else {
      planningCall = this.seanceService.getMonPlanning(debut, fin);
    }

    planningCall.subscribe({
      next: (planning: PlanningDTO) => {
        this.seances = planning.seances ?? [];
      },
      error: (err) => {
        console.error('Erreur lors du chargement du planning', err);
      }
    });
  }

  getSeancesForSlot(jour: number, heure: number): Seance[] {
    return this.seances.filter(s => {
      const debut = new Date(s.dateHeureDebut);
      const fin = new Date(s.dateHeureFin);
      const slotDate = new Date(this.weekStart);
      slotDate.setDate(slotDate.getDate() + jour);

      return debut.getHours() === heure &&
             debut.toDateString() === slotDate.toDateString();
    });
  }

  showSeanceDetail(seance: Seance): void {
    this.selectedSeanceDetail = seance;
    this.showDetailPanel = true;
  }

  closeDetailPanel(): void {
    this.showDetailPanel = false;
    this.selectedSeanceDetail = null;
  }

  getStatutClass(statut: string): string {
    switch (statut) {
      case 'PLANIFIEE': return 'seance-planifiee';
      case 'EN_COURS': return 'seance-en-cours';
      case 'TERMINEE': return 'seance-terminee';
      case 'ANNULEE': return 'seance-annulee';
      default: return '';
    }
  }

  getDayDate(dayIndex: number): string {
    const d = new Date(this.weekStart);
    d.setDate(d.getDate() + dayIndex);
    return d.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' });
  }

  getWeekLabel(): string {
    return `Semaine du ${this.weekStart.toLocaleDateString('fr-FR')} au ${this.weekEnd.toLocaleDateString('fr-FR')}`;
  }

  canSaisirPresences(): boolean {
    return this.userRole === 'FORMATEUR';
  }

  navigateToPresences(seance: Seance): void {
    // Will be implemented with routing
    window.location.href = `/formateur/seances/${seance.id}/presences`;
  }
}
