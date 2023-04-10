import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ExtraUserService } from '../service/extra-user.service';

import { ExtraUserComponent } from './extra-user.component';

describe('ExtraUser Management Component', () => {
  let comp: ExtraUserComponent;
  let fixture: ComponentFixture<ExtraUserComponent>;
  let service: ExtraUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'extra-user', component: ExtraUserComponent }]), HttpClientTestingModule],
      declarations: [ExtraUserComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ExtraUserComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ExtraUserComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ExtraUserService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.extraUsers?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to extraUserService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getExtraUserIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getExtraUserIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
