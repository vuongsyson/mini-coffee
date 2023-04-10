import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'extra-user',
        data: { pageTitle: 'gatewayApp.extraUser.home.title' },
        loadChildren: () => import('./extra-user/extra-user.module').then(m => m.ExtraUserModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
