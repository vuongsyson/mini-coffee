import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IExtraUser, NewExtraUser } from '../extra-user.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IExtraUser for edit and NewExtraUserFormGroupInput for create.
 */
type ExtraUserFormGroupInput = IExtraUser | PartialWithRequiredKeyOf<NewExtraUser>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IExtraUser | NewExtraUser> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type ExtraUserFormRawValue = FormValueOf<IExtraUser>;

type NewExtraUserFormRawValue = FormValueOf<NewExtraUser>;

type ExtraUserFormDefaults = Pick<NewExtraUser, 'id' | 'createdDate' | 'lastModifiedDate'>;

type ExtraUserFormGroupContent = {
  id: FormControl<ExtraUserFormRawValue['id'] | NewExtraUser['id']>;
  shopId: FormControl<ExtraUserFormRawValue['shopId']>;
  fullName: FormControl<ExtraUserFormRawValue['fullName']>;
  phone: FormControl<ExtraUserFormRawValue['phone']>;
  address: FormControl<ExtraUserFormRawValue['address']>;
  point: FormControl<ExtraUserFormRawValue['point']>;
  createdBy: FormControl<ExtraUserFormRawValue['createdBy']>;
  createdDate: FormControl<ExtraUserFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<ExtraUserFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<ExtraUserFormRawValue['lastModifiedDate']>;
  internalUser: FormControl<ExtraUserFormRawValue['internalUser']>;
};

export type ExtraUserFormGroup = FormGroup<ExtraUserFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ExtraUserFormService {
  createExtraUserFormGroup(extraUser: ExtraUserFormGroupInput = { id: null }): ExtraUserFormGroup {
    const extraUserRawValue = this.convertExtraUserToExtraUserRawValue({
      ...this.getFormDefaults(),
      ...extraUser,
    });
    return new FormGroup<ExtraUserFormGroupContent>({
      id: new FormControl(
        { value: extraUserRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      shopId: new FormControl(extraUserRawValue.shopId),
      fullName: new FormControl(extraUserRawValue.fullName, {
        validators: [Validators.required],
      }),
      phone: new FormControl(extraUserRawValue.phone, {
        validators: [Validators.required],
      }),
      address: new FormControl(extraUserRawValue.address, {
        validators: [Validators.required],
      }),
      point: new FormControl(extraUserRawValue.point),
      createdBy: new FormControl(extraUserRawValue.createdBy),
      createdDate: new FormControl(extraUserRawValue.createdDate),
      lastModifiedBy: new FormControl(extraUserRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(extraUserRawValue.lastModifiedDate),
      internalUser: new FormControl(extraUserRawValue.internalUser),
    });
  }

  getExtraUser(form: ExtraUserFormGroup): IExtraUser | NewExtraUser {
    return this.convertExtraUserRawValueToExtraUser(form.getRawValue() as ExtraUserFormRawValue | NewExtraUserFormRawValue);
  }

  resetForm(form: ExtraUserFormGroup, extraUser: ExtraUserFormGroupInput): void {
    const extraUserRawValue = this.convertExtraUserToExtraUserRawValue({ ...this.getFormDefaults(), ...extraUser });
    form.reset(
      {
        ...extraUserRawValue,
        id: { value: extraUserRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ExtraUserFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertExtraUserRawValueToExtraUser(rawExtraUser: ExtraUserFormRawValue | NewExtraUserFormRawValue): IExtraUser | NewExtraUser {
    return {
      ...rawExtraUser,
      createdDate: dayjs(rawExtraUser.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawExtraUser.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertExtraUserToExtraUserRawValue(
    extraUser: IExtraUser | (Partial<NewExtraUser> & ExtraUserFormDefaults)
  ): ExtraUserFormRawValue | PartialWithRequiredKeyOf<NewExtraUserFormRawValue> {
    return {
      ...extraUser,
      createdDate: extraUser.createdDate ? extraUser.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: extraUser.lastModifiedDate ? extraUser.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
