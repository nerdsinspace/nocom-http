export class JsUtils {
  /**
   * Find the first non null element
   * @param args list of nullable elements
   */
  public static coalesce(...args: any): any {
    return args.find(_ => ![undefined, null].includes(_));
  }

  /**
   * Call a function that maybe null
   * @param func nullable function
   */
  public static call(func: any): any {
    if (func != null) {
      return func();
    }
    return null;
  }
}
