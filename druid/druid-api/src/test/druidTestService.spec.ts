import chai from "chai";
import chaiHttp from "chai-http";
import nock from "nock";
import app from "../app";
import { Fixtures } from "./Fixtures";

let should = chai.should();
chai.use(chaiHttp);

describe("/POST druid/v2/", () => {

    beforeEach(() => {
        nock("http://11.2.1.20:8082")
            .post("/druid/v2/")
            .reply(200, {});
    });

    it("When valid request is passed, Should fetch result from the druid", (done) => {
        chai.request(app)
            .post("/druid/v2/")
            .send(JSON.parse(Fixtures.VALID_REQ))
            .end((err, res) => {
                res.should.have.status(200);
                done();
            });
    });

});
