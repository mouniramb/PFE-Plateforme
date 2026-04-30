import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard = (allowedRoles: string[]): CanActivateFn => () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const user = authService.getCurrentUser();

  if (!authService.isLoggedIn() || !user) {
    return router.createUrlTree(['/login']);
  }

  if (allowedRoles.includes(user.role)) {
    return true;
  }

  if (authService.isAdmin()) {
    return router.createUrlTree(['/admin/dashboard']);
  }

  if (authService.isFormateur()) {
    return router.createUrlTree(['/formateur/dashboard']);
  }

  if (authService.isApprenant()) {
    return router.createUrlTree(['/catalogue']);
  }

  return router.createUrlTree(['/login']);
};
