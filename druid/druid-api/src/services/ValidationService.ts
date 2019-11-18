import { ILimits, IValidationResponse } from "../models/models";
export class ValidationService {
    public static validate(request: any, limits: ILimits): IValidationResponse {
        console.log("Request" + request);
        const result: IValidationResponse = { status: true };
        return result;
    }

}
