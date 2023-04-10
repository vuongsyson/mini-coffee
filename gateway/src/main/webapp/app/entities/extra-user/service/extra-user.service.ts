import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IExtraUser, NewExtraUser } from '../extra-user.model';

export type PartialUpdateExtraUser = Partial<IExtraUser> & Pick<IExtraUser, 'id'>;

type RestOf<T extends IExtraUser | NewExtraUser> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestExtraUser = RestOf<IExtraUser>;

export type NewRestExtraUser = RestOf<NewExtraUser>;

export type PartialUpdateRestExtraUser = RestOf<PartialUpdateExtraUser>;

export type EntityResponseType = HttpResponse<IExtraUser>;
export type EntityArrayResponseType = HttpResponse<IExtraUser[]>;

@Injectable({ providedIn: 'root' })
export class ExtraUserService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/extra-users');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(extraUser: NewExtraUser): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(extraUser);
    return this.http
      .post<RestExtraUser>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(extraUser: IExtraUser): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(extraUser);
    return this.http
      .put<RestExtraUser>(`${this.resourceUrl}/${this.getExtraUserIdentifier(extraUser)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(extraUser: PartialUpdateExtraUser): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(extraUser);
    return this.http
      .patch<RestExtraUser>(`${this.resourceUrl}/${this.getExtraUserIdentifier(extraUser)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestExtraUser>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestExtraUser[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getExtraUserIdentifier(extraUser: Pick<IExtraUser, 'id'>): number {
    return extraUser.id;
  }

  compareExtraUser(o1: Pick<IExtraUser, 'id'> | null, o2: Pick<IExtraUser, 'id'> | null): boolean {
    return o1 && o2 ? this.getExtraUserIdentifier(o1) === this.getExtraUserIdentifier(o2) : o1 === o2;
  }

  addExtraUserToCollectionIfMissing<Type extends Pick<IExtraUser, 'id'>>(
    extraUserCollection: Type[],
    ...extraUsersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const extraUsers: Type[] = extraUsersToCheck.filter(isPresent);
    if (extraUsers.length > 0) {
      const extraUserCollectionIdentifiers = extraUserCollection.map(extraUserItem => this.getExtraUserIdentifier(extraUserItem)!);
      const extraUsersToAdd = extraUsers.filter(extraUserItem => {
        const extraUserIdentifier = this.getExtraUserIdentifier(extraUserItem);
        if (extraUserCollectionIdentifiers.includes(extraUserIdentifier)) {
          return false;
        }
        extraUserCollectionIdentifiers.push(extraUserIdentifier);
        return true;
      });
      return [...extraUsersToAdd, ...extraUserCollection];
    }
    return extraUserCollection;
  }

  protected convertDateFromClient<T extends IExtraUser | NewExtraUser | PartialUpdateExtraUser>(extraUser: T): RestOf<T> {
    return {
      ...extraUser,
      createdDate: extraUser.createdDate?.toJSON() ?? null,
      lastModifiedDate: extraUser.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restExtraUser: RestExtraUser): IExtraUser {
    return {
      ...restExtraUser,
      createdDate: restExtraUser.createdDate ? dayjs(restExtraUser.createdDate) : undefined,
      lastModifiedDate: restExtraUser.lastModifiedDate ? dayjs(restExtraUser.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestExtraUser>): HttpResponse<IExtraUser> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestExtraUser[]>): HttpResponse<IExtraUser[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
