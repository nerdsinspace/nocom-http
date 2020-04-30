import { Hit } from './hit';

export enum Dimension {
  NETHER = -1,
  OVERWORLD,
  END
}

export function getDimensionIdByName(dimension: string): Dimension {
  switch (dimension.toLowerCase()) {
    case 'nether':
      return Dimension.NETHER;
    case 'overworld':
      return Dimension.OVERWORLD;
    case 'end':
      return Dimension.END;
  }
  return null;
}

export function fixDimensionMember(o: {dimension?: Dimension | string}) {
  // the data from the api uses strings to represent dimensions
  // we must convert them to the enum type
  if (typeof o.dimension === 'string') {
    o.dimension = Dimension[o.dimension];
  }
}
