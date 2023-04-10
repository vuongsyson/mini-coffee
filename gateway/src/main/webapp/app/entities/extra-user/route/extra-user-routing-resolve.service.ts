import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IExtraUser } from '../extra-user.model';
import { ExtraUserService } from '../service/extra-user.service';

@Injectable({ providedIn: 'root' })
export class ExtraUserRoutingResolveService implements Resolve<IExtraUser | null> {
  constructor(protected service: ExtraUserService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IExtraUser | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((extraUser: HttpResponse<IExtraUser>) => {
          if (extraUser.body) {
            return of(extraUser.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
