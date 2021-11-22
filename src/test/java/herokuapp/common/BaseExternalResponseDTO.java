package herokuapp.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

/**
 * Created by daluzl on 3/21/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseExternalResponseDTO<T> {
    @JsonIgnore
    protected String cached;
    @JsonIgnore
    protected Long processingTimeMs;

    protected T content;
    protected List<T> results;
    protected String transactionId;
    protected String id;
    private String errMessage;
    private String exception;
    private Map<String, Object> validationFailure;
    private byte[] bytes;
    private Integer totalHits;
    private Integer hits;
    private Integer size;
    private Integer page;

    public BaseExternalResponseDTO() {
        // meh
    }

    public String getCached() {
        return cached;
    }

    public void setCached(String cached) {
        this.cached = cached;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public Long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(Long processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Map<String, Object> getValidationFailure() {
        return validationFailure;
    }

    public void setValidationFailure(Map<String, Object> validationFailure) {
        this.validationFailure = validationFailure;
    }

    public Integer getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(Integer totalHits) {
        this.totalHits = totalHits;
    }

    public String getTransactionId() {
        return transactionId;
    }
}