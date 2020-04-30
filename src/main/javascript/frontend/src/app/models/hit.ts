import { Dimension } from './dimension.enum';
import { Point } from './point';

export interface Hit extends Point {
  trackId?: number;
  dimension?: Dimension;
  createdAt?: Date;

  _updated?: boolean;
}
