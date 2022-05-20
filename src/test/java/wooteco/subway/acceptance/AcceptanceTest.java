package wooteco.subway.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import wooteco.subway.dto.SectionRequest;
import wooteco.subway.dto.StationRequest;
import wooteco.subway.dto.StationResponse;
import wooteco.subway.dto.line.LineResponse;
import wooteco.subway.dto.line.LineSaveRequest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AcceptanceTest {

    protected static final String NOT_FOUND_ERROR_MESSAGE = "존재하지 않습니다";
    protected static final String BLANK_OR_NULL_ERROR_MESSAGE = "빈 값";
    private static final String LINE_BASE_URI = "/lines";
    private static final String STATION_BASE_URI = "/stations";
    public static final String SECTION_BASE_URI = "/sections";

    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    protected ExtractableResponse<Response> createStationAndReturnResponse(final String stationName) {
        return post(new StationRequest(stationName), STATION_BASE_URI);
    }

    protected StationResponse createStation(final String stationName) {
        return post(new StationRequest(stationName), STATION_BASE_URI).as(StationResponse.class);
    }

    protected LineResponse createLine(final String name, final String color, final long upStationId,
                                      final long downStationId, final int distance
    ) {
        return post(new LineSaveRequest(name, color, upStationId, downStationId, distance), LINE_BASE_URI).as(
                LineResponse.class);
    }

    protected LineResponse createLine(final String name, final String color, final Long upStationId,
                                      final Long downStationId, final int distance, final int extraFare) {
        return post(new LineSaveRequest(name, color, upStationId, downStationId, distance, extraFare),
                LINE_BASE_URI).as(LineResponse.class);
    }

    protected ExtractableResponse<Response> createLineAndReturnResponse(final String name, final String color,
                                                                        final Long upStationId,
                                                                        final Long downStationId,
                                                                        final int distance,
                                                                        final int extraFare) {

        return post(new LineSaveRequest(name, color, upStationId, downStationId, distance, extraFare), LINE_BASE_URI);
    }

    protected ExtractableResponse<Response> createLineAndReturnResponse(
            final String name, final String color, final long upStationId, final long downStationId, final int distance
    ) {
        return post(new LineSaveRequest(name, color, upStationId, downStationId, distance), LINE_BASE_URI);
    }

    protected void addSection(final LineResponse lineResponse, final StationResponse upStationResponse,
                              final StationResponse downStationResponse, final int distance) {

        post(new SectionRequest(upStationResponse.getId(), downStationResponse.getId(), distance),
                LINE_BASE_URI + lineResponse.getId() + SECTION_BASE_URI);
    }

    private <T> ExtractableResponse<Response> post(final T requestEntity, final String uri) {
        return RestAssured
                .given()
                .body(requestEntity)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(uri)
                .then()
                .extract();
    }
}
