import { IValidationResponse } from "../models/ErrorResponse";
export class ValidationService {
    public static validate(limit: any): IValidationResponse {
        const result: IValidationResponse = { status: false };
        return result;
    }

}
