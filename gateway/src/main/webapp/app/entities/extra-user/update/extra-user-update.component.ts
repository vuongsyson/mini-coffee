import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ExtraUserFormService, ExtraUserFormGroup } from './extra-user-form.service';
import { IExtraUser } from '../extra-user.model';
import { ExtraUserService } from '../service/extra-user.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'shop-extra-user-update',
  templateUrl: './extra-user-update.component.html',
})
export class ExtraUserUpdateComponent implements OnInit {
  isSaving = false;
  extraUser: IExtraUser | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: ExtraUserFormGroup = this.extraUserFormService.createExtraUserFormGroup();

  constructor(
    protected extraUserService: ExtraUserService,
    protected extraUserFormService: ExtraUserFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ extraUser }) => {
      this.extraUser = extraUser;
      if (extraUser) {
        this.updateForm(extraUser);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const extraUser = this.extraUserFormService.getExtraUser(this.editForm);
    if (extraUser.id !== null) {
      this.subscribeToSaveResponse(this.extraUserService.update(extraUser));
    } else {
      this.subscribeToSaveResponse(this.extraUserService.create(extraUser));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExtraUser>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(extraUser: IExtraUser): void {
    this.extraUser = extraUser;
    this.extraUserFormService.resetForm(this.editForm, extraUser);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, extraUser.internalUser);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.extraUser?.internalUser)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
