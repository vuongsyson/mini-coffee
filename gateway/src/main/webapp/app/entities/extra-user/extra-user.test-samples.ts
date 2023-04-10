import dayjs from 'dayjs/esm';

import { IExtraUser, NewExtraUser } from './extra-user.model';

export const sampleWithRequiredData: IExtraUser = {
  id: 68217,
  fullName: 'Uruguay Louisiana South',
  phone: '1-278-903-1156 x615',
  address: 'national Bermuda',
};

export const sampleWithPartialData: IExtraUser = {
  id: 53244,
  shopId: 35645,
  fullName: 'calculating',
  phone: '902-886-7004 x5594',
  address: 'orchid upward-trending',
  createdBy: 'Steel Afghani',
  lastModifiedBy: 'Usability Bike copying',
  lastModifiedDate: dayjs('2023-03-20T04:26'),
};

export const sampleWithFullData: IExtraUser = {
  id: 78186,
  shopId: 12942,
  fullName: 'Dale deposit mobile',
  phone: '(537) 955-7498 x1589',
  address: 'benchmark Producer',
  point: 90691,
  createdBy: 'Synergistic microchip Nebraska',
  createdDate: dayjs('2023-03-19T12:42'),
  lastModifiedBy: 'Handcrafted Frozen',
  lastModifiedDate: dayjs('2023-03-19T07:41'),
};

export const sampleWithNewData: NewExtraUser = {
  fullName: 'Usability',
  phone: '472.782.1616 x187',
  address: 'optical Avenue',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
