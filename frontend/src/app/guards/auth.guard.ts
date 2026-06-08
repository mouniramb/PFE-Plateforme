import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    return router.createUrlTree(['/login']);
  }

  // Protect admin area from non-admin users.
  if (state.url.startsWith('/admin') && !authService.isAdmin()) {
    return router.createUrlTree(['/login']);
  }

  return true;
};

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn() && authService.isAdmin()) {
    return true;
  }

  router.navigate(['/login']);
  return false;
};

// Redirige les utilisateurs déjà connectés vers leur dashboard
export const publicGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isLoggedIn()) {
    return true;
  }

  const role = authService.getUserRole();
  if (role === 'ADMIN') {
    return router.createUrlTree(['/admin/dashboard']);
  } else if (role === 'FORMATEUR') {
    return router.createUrlTree(['/formateur/dashboard']);
  } else if (role === 'APPRENANT') {
    return router.createUrlTree(['/catalogue']);
  }

  return true;
};
