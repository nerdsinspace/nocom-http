import { Dimension } from './dimension.enum';
import { Hit } from './hit';

export interface TrackHistory {
  trackId: number;
  dimension: Dimension;
  server?: string;
  hits: Hit[];
}
