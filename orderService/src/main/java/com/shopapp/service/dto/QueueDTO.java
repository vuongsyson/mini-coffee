package com.shopapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.shopapp.domain.Queue} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class QueueDTO implements Serializable {

    private Long id;

    @NotNull
    private Long shopId;

    private String code;

    private Long[] queueArray;

    @NotNull
    private Boolean active;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long[] getQueueArray() {
        return queueArray;
    }

    public void setQueueArray(Long[] queueArray) {
        this.queueArray = queueArray;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QueueDTO)) {
            return false;
        }

        QueueDTO queueDTO = (QueueDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, queueDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "QueueDTO{" +
            "id=" + getId() +
            ", shopId=" + getShopId() +
            ", code='" + getCode() + "'" +
            ", queueArray=" + Arrays.toString(getQueueArray()) + "'" +
            ", active='" + getActive() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
