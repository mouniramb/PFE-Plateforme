import { Routes } from '@angular/router';
import { adminGuard, publicGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { FormateurLayoutComponent } from './layouts/formateur-layout/formateur-layout.component';
import { AdminDetailApprenantComponent } from './components/admin-detail-apprenant/admin-detail-apprenant.component';
import { ApprenantLayoutComponent } from './layouts/apprenant-layout/apprenant-layout.component';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./components/home/home.component').then(m => m.HomeComponent),
    canActivate: [publicGuard],
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./components/login/login.component').then(m => m.LoginComponent),
    canActivate: [publicGuard]
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./components/register/register.component').then(m => m.RegisterComponent),
    canActivate: [publicGuard]
  },
  // ── Route détail apprenant : accessible ADMIN + FORMATEUR ───────────
  // Placée avant le groupe admin pour éviter le blocage canActivateChild
  {
    path: 'admin/formations/:formationId/suivi/apprenant/:apprenantId',
    canActivate: [roleGuard(['ADMIN', 'FORMATEUR'])],
    component: AdminDetailApprenantComponent
  },
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [adminGuard],
    canActivateChild: [adminGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./components/admin-dashboard/admin-dashboard.component').then(
            m => m.AdminDashboardComponent
          )
      },
      {
        path: 'formateurs',
        loadComponent: () =>
          import('./components/admin-formateurs/admin-formateurs.component').then(
            m => m.AdminFormateursComponent
          )
      },
      {
        path: 'formations',
        loadComponent: () =>
          import('./components/admin-formations/admin-formations.component').then(
            m => m.AdminFormationsComponent
          )
      },
      {
        path: 'formations/create',
        loadComponent: () =>
          import('./components/formation-create-edit/formation-create-edit.component').then(
            m => m.FormationCreateEditComponent
          )
      },
      {
        path: 'formations/edit/:id',
        loadComponent: () =>
          import('./components/formation-create-edit/formation-create-edit.component').then(
            m => m.FormationCreateEditComponent
          )
      },
      {
        path: 'inscriptions',
        loadComponent: () =>
          import('./components/admin-inscriptions/admin-inscriptions.component').then(
            m => m.AdminInscriptionsComponent
          )
      },
      {
        path: 'formations/:id/inscrits',
        loadComponent: () =>
          import('./components/inscrits-formation/inscrits-formation.component').then(
            m => m.InscritsFormationComponent
          )
      },
      {
        path: 'planning',
        loadComponent: () =>
          import('./components/planning-calendrier/planning-calendrier.component').then(
            m => m.PlanningCalendrierComponent
          )
      },
      {
        path: 'salles',
        loadComponent: () =>
          import('./components/admin-salles/admin-salles.component').then(
            m => m.AdminSallesComponent
          )
      },
      {
        path: 'seances',
        loadComponent: () =>
          import('./components/admin-seances/admin-seances.component').then(
            m => m.AdminSeancesComponent
          )
      },
      {
        path: 'formations/:id/suivi',
        loadComponent: () =>
          import('./components/suivi-formation/suivi-formation.component').then(
            m => m.SuiviFormationComponent
          )
      },
      {
        path: 'paiements',
        loadComponent: () =>
          import('./components/admin-paiements/admin-paiements.component').then(
            m => m.AdminPaiementsComponent
          )
      },
      {
        path: 'paiements/enregistrer',
        loadComponent: () => import('./components/enregistrer-paiement/enregistrer-paiement.component').then(m => m.EnregistrerPaiementComponent)
      },
      {
        path: 'tarifs',
        loadComponent: () =>
          import('./components/admin-tarifs/admin-tarifs.component').then(
            m => m.AdminTarifsComponent
          )
      },
      {
        path: 'factures',
        loadComponent: () =>
          import('./components/admin-factures/admin-factures.component').then(
            m => m.AdminFacturesComponent
          )
      },
      {
        path: 'rapports',
        loadComponent: () =>
          import('./components/admin-rapports/admin-rapports.component').then(
            m => m.AdminRapportsComponent
          )
      },
      {
        path: 'statistiques',
        loadComponent: () =>
          import('./components/admin-statistiques/admin-statistiques.component').then(
            m => m.AdminStatistiquesComponent
          )
      },
    ]
  },
  {
    path: 'formateur',
    component: FormateurLayoutComponent,
    canActivate: [roleGuard(['FORMATEUR', 'ADMIN'])],
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./components/formateur-dashboard/formateur-dashboard.component').then(
            m => m.FormateurDashboardComponent
          )
      },
      {
        path: 'formations/:id/inscrits',
        loadComponent: () =>
          import('./components/inscrits-formation/inscrits-formation.component').then(
            m => m.InscritsFormationComponent
          )
      },
      {
        path: 'planning',
        loadComponent: () =>
          import('./components/planning-calendrier/planning-calendrier.component').then(
            m => m.PlanningCalendrierComponent
          )
      },
      {
        path: 'seances/:seanceId/presences',
        loadComponent: () =>
          import('./components/saisie-presences/saisie-presences.component').then(
            m => m.SaisiePresencesComponent
          )
      },
      {
        path: 'seances/:seanceId/notes',
        loadComponent: () =>
          import('./components/saisie-notes/saisie-notes.component').then(
            m => m.SaisieNotesComponent
          )
      },
      {
        path: 'formations/:id/suivi',
        loadComponent: () =>
          import('./components/suivi-formation/suivi-formation.component').then(
            m => m.SuiviFormationComponent
          )
      },
      {
        path: 'paiements',
        loadComponent: () =>
          import('./components/formateur-paiements/formateur-paiements.component').then(
            m => m.FormateurPaiementsComponent
          )
      },
      {
        path: 'revenus',
        loadComponent: () =>
          import('./components/formateur-revenus/formateur-revenus.component').then(
            m => m.FormateurRevenusComponent
          )
      },
      {
        path: 'presences',
        loadComponent: () =>
          import('./components/formateur-presences/formateur-presences.component').then(
            m => m.FormateurPresencesComponent
          )
      },
      {
        path: 'notes',
        loadComponent: () =>
          import('./components/formateur-notes/formateur-notes.component').then(
            m => m.FormateurNotesComponent
          )
      },
      {
        path: 'suivi',
        loadComponent: () =>
          import('./components/formateur-suivi/formateur-suivi.component').then(
            m => m.FormateurSuiviComponent
          )
      },
      {
        path: 'paiements/enregistrer',
        loadComponent: () =>
          import('./components/enregistrer-paiement/enregistrer-paiement.component').then(
            m => m.EnregistrerPaiementComponent
          )
      }
    ]
  },
  // ── Layout apprenant : navbar fixe partagée (accessible à tous) ────
  {
    path: '',
    component: ApprenantLayoutComponent,
    children: [
      // ── Routes publiques (accessible sans connexion) ──────────────
      {
        path: 'catalogue',
        loadComponent: () =>
          import('./components/catalogue-formations/catalogue-formations.component').then(
            m => m.CatalogueFormationsComponent
          )
      },
      {
        path: 'formations/:id',
        loadComponent: () =>
          import('./components/formation-detail/formation-detail.component').then(
            m => m.FormationDetailComponent
          )
      },
      // ── Routes privées (apprenant uniquement) ────────────────────
      {
        path: 'mes-inscriptions',
        canActivate: [roleGuard(['APPRENANT'])],
        loadComponent: () =>
          import('./components/mes-inscriptions/mes-inscriptions.component').then(
            m => m.MesInscriptionsComponent
          )
      },
      {
        path: 'mon-planning',
        canActivate: [roleGuard(['APPRENANT'])],
        loadComponent: () =>
          import('./components/planning-calendrier/planning-calendrier.component').then(
            m => m.PlanningCalendrierComponent
          )
      },
      {
        path: 'mes-notes',
        canActivate: [roleGuard(['APPRENANT'])],
        loadComponent: () =>
          import('./components/mes-notes-apprenant/mes-notes-apprenant.component').then(
            m => m.MesNotesApprenantComponent
          )
      },
      {
        path: 'mes-presences',
        canActivate: [roleGuard(['APPRENANT'])],
        loadComponent: () =>
          import('./components/mes-presences-apprenant/mes-presences-apprenant.component').then(
            m => m.MesPresencesApprenantComponent
          )
      },
      {
        path: 'mon-suivi',
        canActivate: [roleGuard(['APPRENANT'])],
        loadComponent: () =>
          import('./components/suivi-apprenant/suivi-apprenant.component').then(
            m => m.SuiviApprenantComponent
          )
      },
      {
        path: 'mes-paiements',
        canActivate: [roleGuard(['APPRENANT'])],
        loadComponent: () =>
          import('./components/mes-paiements-apprenant/mes-paiements-apprenant.component').then(
            m => m.MesPaiementsApprenantComponent
          )
      },
      {
        path: 'mes-paiements-detail',
        canActivate: [roleGuard(['APPRENANT'])],
        loadComponent: () =>
          import('./components/apprenant-paiements/apprenant-paiements.component').then(
            m => m.ApprenantPaiementsComponent
          )
      }
    ]
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];
