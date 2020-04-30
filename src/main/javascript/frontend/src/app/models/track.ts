import { Dimension } from './dimension.enum';
import { Hit } from './hit';

export interface Track extends Hit {
  server?: string;
  previousTrackId?: number;
  previousDimension?: Dimension;
}
