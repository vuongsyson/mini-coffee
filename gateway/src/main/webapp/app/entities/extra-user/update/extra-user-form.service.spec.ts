import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../extra-user.test-samples';

import { ExtraUserFormService } from './extra-user-form.service';

describe('ExtraUser Form Service', () => {
  let service: ExtraUserFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExtraUserFormService);
  });

  describe('Service methods', () => {
    describe('createExtraUserFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createExtraUserFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            shopId: expect.any(Object),
            fullName: expect.any(Object),
            phone: expect.any(Object),
            address: expect.any(Object),
            point: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
            internalUser: expect.any(Object),
          })
        );
      });

      it('passing IExtraUser should create a new form with FormGroup', () => {
        const formGroup = service.createExtraUserFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            shopId: expect.any(Object),
            fullName: expect.any(Object),
            phone: expect.any(Object),
            address: expect.any(Object),
            point: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
            internalUser: expect.any(Object),
          })
        );
      });
    });

    describe('getExtraUser', () => {
      it('should return NewExtraUser for default ExtraUser initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createExtraUserFormGroup(sampleWithNewData);

        const extraUser = service.getExtraUser(formGroup) as any;

        expect(extraUser).toMatchObject(sampleWithNewData);
      });

      it('should return NewExtraUser for empty ExtraUser initial value', () => {
        const formGroup = service.createExtraUserFormGroup();

        const extraUser = service.getExtraUser(formGroup) as any;

        expect(extraUser).toMatchObject({});
      });

      it('should return IExtraUser', () => {
        const formGroup = service.createExtraUserFormGroup(sampleWithRequiredData);

        const extraUser = service.getExtraUser(formGroup) as any;

        expect(extraUser).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IExtraUser should not enable id FormControl', () => {
        const formGroup = service.createExtraUserFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewExtraUser should disable id FormControl', () => {
        const formGroup = service.createExtraUserFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
