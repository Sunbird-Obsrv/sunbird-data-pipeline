import chai from "chai";
import chaiHttp from "chai-http";
import HttpStatus from "http-status-codes";
import nock from "nock";
import app from "../app";
import { config } from "./config";
import { Fixtures } from "./fixtures";

const expect = chai.expect;

const should = chai.should();
chai.use(chaiHttp);

describe("/POST druid/v2/", () => {

    beforeEach(() => {
        nock(config.druidHost + ":" + config.druidPort)
            .post(config.druidEndPoint)
            .reply(200, {});
    });

    it("Should fetch the result from the druid telemetry data source, When valid request is passed", (done) => {
        chai.request(app)
            .post(config.apiEndPoint)
            .send(JSON.parse(Fixtures.TELEMETRY_DATA_SOURCE_VALID_QUERY))
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
                res.should.have.status(HttpStatus.FORBIDDEN);
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
                res.should.have.status(HttpStatus.FORBIDDEN);
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
                res.should.have.status(HttpStatus.FORBIDDEN);
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
                res.should.have.status(HttpStatus.FORBIDDEN);
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
                res.should.have.status(HttpStatus.FORBIDDEN);
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
                res.should.have.status(HttpStatus.FORBIDDEN);
                expect(res.body).not.equal(undefined);
                expect(res.body.errorMessage).not.equal(undefined);
                done();
            });
    });
    // tslint:disable-next-line:max-line-length
    it("Should take default threshold, When threshold is higher than the limit in the timeBoundary queryType", (done) => {
        chai.request(app)
            .post(config.apiEndPoint)
            .send(JSON.parse(Fixtures.TIME_BOUNDARY_HIGH_THRESHOLD_QUERY))
            .end((err, res) => {
                res.should.have.status(HttpStatus.OK);
                expect(res.body).not.equal(undefined);
                expect(res.body.errorMessage).equal(undefined);
                done();
            });
    });

    it("Should allow user to query, When the limits are not found for particular data source", (done) => {
        chai.request(app)
            .post(config.apiEndPoint)
            .send(JSON.parse(Fixtures.UNSUPPORTED_DATA_SOURCE))
            .end((err, res) => {
                res.should.have.status(HttpStatus.OK);
                expect(res.body).not.equal(undefined);
                expect(res.body.errorMessage).equal(undefined);
                done();
            });
    });

    it("Should fetch the result from the druid summary data source, When valid request is passed", (done) => {
        chai.request(app)
            .post(config.apiEndPoint)
            .send(JSON.parse(Fixtures.SUMMARY_DATA_SOURCE_VALID_QUERY))
            .end((err, res) => {
                res.should.have.status(HttpStatus.OK);
                done();
            });
    });

});
describe("/GET druid/v2/datasource", () => {
    const response = ["summary-events", "telemetry-error-events", "content-model-snapshot", "telemetry-events-multi", "telemetry-cdata", "telemetry-rollup-theta5", "telemetry-events-test", "telemetry-rollup-test", "telemetry-events-old", "telemetry-feedback-events", "pipeline-metrics", "telemetry-lessqueryg", "telemetry-rollup-theta4"];
    beforeEach(() => {
        nock(config.druidHost + ":" + config.druidPort)
            .get(config.druidDataSourceEndPoint)
            .reply(200, response);
    });

    it("Should fetch the list of data source which are available, When get source api is inovked", (done) => {
        chai.request(app)
            .get(config.apiDataSourceEndPoint)
            .end((err, res) => {
                res.should.have.status(HttpStatus.OK);
                expect(res.body).not.eq(undefined);
                expect(res.body.length).eq(13);
                done();
            });
    });
});

describe("/POST druid/v2/cql", () => {
    // tslint:disable-next-line: max-line-length
    const response = [{ "contentId": "do_11272916574416076817226", "totalRatingsCount": 1, "Total Ratings": 5, "averageRating": 5 }, { "contentId": "do_112768268386615296159", "totalRatingsCount": 1, "Total Ratings": 5, "averageRating": 5 }, { "contentId": "do_112797193459564544172", "totalRatingsCount": 1, "Total Ratings": 5, "averageRating": 5 }, { "contentId": "do_11283193441064550414", "totalRatingsCount": 4, "Total Ratings": 20, "averageRating": 5 }, { "contentId": "do_1128862117518868481111", "totalRatingsCount": 4, "Total Ratings": 20, "averageRating": 5 }, { "contentId": "domain_14461", "totalRatingsCount": 1, "Total Ratings": 5, "averageRating": 5 }];
    beforeEach(() => {
        nock(config.druidHost + ":" + config.druidPort)
            .post(config.druidSqlEndPoint)
            .reply(200, response);
    });

    it("Should able to query the cql, When auth token is valid", (done) => {
        chai.request(app)
            .post(config.apiSqlEndPoint)
            .end((err, res) => {
                res.should.have.status(HttpStatus.OK);
                expect(res.body).not.eq(undefined);
                done();
            });
    });
});

describe("/POST druid/status", () => {
    // tslint:disable-next-line: max-line-length
    beforeEach(() => {
        nock(config.druidHost + ":" + config.druidPort)
            .get(config.druidStatus)
            .reply(200, {});
    });

    it("Should able to query the cql, When auth token is valid", (done) => {
        chai.request(app)
            .get(config.apiDruidStatus)
            .end((err, res) => {
                res.should.have.status(HttpStatus.OK);
                expect(res.body).not.eq(undefined);
                done();
            });
    });
});
