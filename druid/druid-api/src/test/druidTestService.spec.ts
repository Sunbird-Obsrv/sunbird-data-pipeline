import chai from "chai";
import chaiHttp from "chai-http";
import HttpStatus from "http-status-codes";
import nock from "nock";
import app from "../app";
import { config } from "../configs/config";
import { Fixtures } from "./Fixtures";

const expect = chai.expect;

const should = chai.should();
chai.use(chaiHttp);

describe("/POST druid/v2/", () => {

    beforeEach(() => {
        nock("http://11.2.1.20:8082")
            .post(config.druidEndPoint)
            .reply(200, {});
    });

    it("Should fetch the result from the druid, When valid request is passed", (done) => {
        chai.request(app)
            .post(config.apiEndPoint)
            .send(JSON.parse(Fixtures.VALID_QUERY))
            .end((err, res) => {
                res.should.have.status(HttpStatus.OK);
                done();
            });
    });

    it("Should reject the query, When dimensions are more than the limit", (done) => {
        chai.request(app)
            .post(config.apiEndPoint)
            .send(JSON.parse(Fixtures.MANY_DIMENSIONS_QUERY))
            .end((err, res) => {
                res.should.have.status(HttpStatus.BAD_REQUEST);
                expect(res.body).not.equal(undefined);
                expect(res.body.errorMessage).not.equal(undefined);
                done();
            });
    });

    it("Should reject query, When date range[Defined in LIST] is higher than the limit", (done) => {
        chai.request(app)
            .post(config.apiEndPoint)
            .send(JSON.parse(Fixtures.HIGH_DATE_RANGE_QUERY_IN_LIST))
            .end((err, res) => {
                res.should.have.status(HttpStatus.BAD_REQUEST);
                expect(res.body).not.equal(undefined);
                expect(res.body.errorMessage).not.equal(undefined);
                done();
            });
    });

    it("Should reject query, When date range[Defined as STRING] is higher than the limit", (done) => {
        chai.request(app)
            .post(config.apiEndPoint)
            .send(JSON.parse(Fixtures.HIGH_DATE_RANGE_QUERY_AS_STRING))
            .end((err, res) => {
                res.should.have.status(HttpStatus.BAD_REQUEST);
                expect(res.body).not.equal(undefined);
                expect(res.body.errorMessage).not.equal(undefined);
                done();
            });
    });

    it("Should reject the query, When filter is having more high cardinal dimensions than the limit", (done) => {
        chai.request(app)
            .post(config.apiEndPoint)
            .send(JSON.parse(Fixtures.FILTER_WITH_HIGHT_CARDINAL_DIMS_QUERY))
            .end((err, res) => {
                res.should.have.status(HttpStatus.BAD_REQUEST);
                expect(res.body).not.equal(undefined);
                expect(res.body.errorMessage).not.equal(undefined);
                done();
            });
    });
    it("Should take default threshold, When threshold is higher than the limit", (done) => {
        chai.request(app)
            .post(config.apiEndPoint)
            .send(JSON.parse(Fixtures.HIGH_THRESHOLD_QUERY))
            .end((err, res) => {
                res.should.have.status(HttpStatus.OK);
                expect(res.body).not.equal(undefined);
                expect(res.body.errorMessage).equal(undefined);
                done();
            });
    });

    it("Should take default threshold, When threshold is not defined", (done) => {
        chai.request(app)
            .post(config.apiEndPoint)
            .send(JSON.parse(Fixtures.WITHOUT_THRESHOLD_QUERY))
            .end((err, res) => {
                res.should.have.status(HttpStatus.OK);
                expect(res.body).not.equal(undefined);
                expect(res.body.errorMessage).equal(undefined);
                done();
            });
    });

    it("Should reject the query, When date range is not defined", (done) => {
        chai.request(app)
            .post(config.apiEndPoint)
            .send(JSON.parse(Fixtures.WITHOUT_DATE_RANGE_QUERY))
            .end((err, res) => {
                res.should.have.status(HttpStatus.BAD_REQUEST);
                expect(res.body).not.equal(undefined);
                expect(res.body.errorMessage).not.equal(undefined);
                done();
            });
    });

    it("Should reject the query, When filter is having list of high cardinal dimensions in list than limit", (done) => {
        chai.request(app)
            .post(config.apiEndPoint)
            .send(JSON.parse(Fixtures.WITHOUT_DATE_RANGE_QUERY))
            .end((err, res) => {
                res.should.have.status(HttpStatus.BAD_REQUEST);
                expect(res.body).not.equal(undefined);
                expect(res.body.errorMessage).not.equal(undefined);
                done();
            });
    });
});
