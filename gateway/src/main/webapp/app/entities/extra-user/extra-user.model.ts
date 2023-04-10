import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface IExtraUser {
  id: number;
  shopId?: number | null;
  fullName?: string | null;
  phone?: string | null;
  address?: string | null;
  point?: number | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  internalUser?: Pick<IUser, 'id'> | null;
}

export type NewExtraUser = Omit<IExtraUser, 'id'> & { id: null };
