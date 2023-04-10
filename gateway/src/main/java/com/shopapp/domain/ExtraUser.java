package com.shopapp.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ExtraUser.
 */
@Table("extra_user")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExtraUser extends AbstractAuditingEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("shop_id")
    private Long shopId;

    @NotNull(message = "must not be null")
    @Column("full_name")
    private String fullName;

    @NotNull(message = "must not be null")
    @Column("phone")
    private String phone;

    @NotNull(message = "must not be null")
    @Column("address")
    private String address;

    @Column("point")
    private Long point;

    // Inherited createdBy definition
    // Inherited createdDate definition
    // Inherited lastModifiedBy definition
    // Inherited lastModifiedDate definition

    @Transient
    private User internalUser;

    @Column("internal_user_id")
    private Long internalUserId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ExtraUser id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShopId() {
        return this.shopId;
    }

    public ExtraUser shopId(Long shopId) {
        this.setShopId(shopId);
        return this;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getFullName() {
        return this.fullName;
    }

    public ExtraUser fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return this.phone;
    }

    public ExtraUser phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return this.address;
    }

    public ExtraUser address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getPoint() {
        return this.point;
    }

    public ExtraUser point(Long point) {
        this.setPoint(point);
        return this;
    }

    public void setPoint(Long point) {
        this.point = point;
    }

    // Inherited createdBy methods
    public ExtraUser createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    // Inherited createdDate methods
    public ExtraUser createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    // Inherited lastModifiedBy methods
    public ExtraUser lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    // Inherited lastModifiedDate methods
    public ExtraUser lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public User getInternalUser() {
        return this.internalUser;
    }

    public void setInternalUser(User user) {
        this.internalUser = user;
        this.internalUserId = user != null ? user.getId() : null;
    }

    public ExtraUser internalUser(User user) {
        this.setInternalUser(user);
        return this;
    }

    public Long getInternalUserId() {
        return this.internalUserId;
    }

    public void setInternalUserId(Long user) {
        this.internalUserId = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExtraUser)) {
            return false;
        }
        return id != null && id.equals(((ExtraUser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExtraUser{" +
            "id=" + getId() +
            ", shopId=" + getShopId() +
            ", fullName='" + getFullName() + "'" +
            ", phone='" + getPhone() + "'" +
            ", address='" + getAddress() + "'" +
            ", point=" + getPoint() +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
