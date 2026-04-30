import { Routes } from '@angular/router';
import { adminGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./components/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./components/register/register.component').then(m => m.RegisterComponent)
  },
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
  {
    path: 'mes-inscriptions',
    canActivate: [roleGuard(['APPRENANT'])],
    loadComponent: () =>
      import('./components/mes-inscriptions/mes-inscriptions.component').then(
        m => m.MesInscriptionsComponent
      )
  },
  {
    path: 'admin',
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
      }
    ]
  },
  {
    path: 'formateur',
    canActivate: [roleGuard(['FORMATEUR', 'ADMIN'])],
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./components/admin-dashboard/admin-dashboard.component').then(
            m => m.AdminDashboardComponent
          )
      },
      {
        path: 'formations/:id/inscrits',
        loadComponent: () =>
          import('./components/inscrits-formation/inscrits-formation.component').then(
            m => m.InscritsFormationComponent
          )
      }
    ]
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];
