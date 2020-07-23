import log4js, { Logger } from "log4js";
import { config } from "../configs/config";

/**
 * Service to log the message for the debug purpose.
 */
class APILoggerService {
    private logger: Logger;
    constructor() {
        log4js.configure({
            // tslint:disable-next-line:max-line-length
            appenders: { druid_proxy_api: { type: "file", filename: config.log.logFilePath, pattern: config.log.pattern, maxLogSize: config.log.maxLogSize, backups: config.log.backups, compress: true } },
            categories: { default: { appenders: ["druid_proxy_api"], level: "debug" } },
        });
        this.logger = log4js.getLogger("druid_proxy_api");
    }
    public log(message: string) {
        this.logger.info(message);
    }
    public error(message: any) {
        this.logger.error(message);
    }
    public warn(message: any) {
        this.logger.warn(message);
    }
}
export const APILogger = new APILoggerService();
