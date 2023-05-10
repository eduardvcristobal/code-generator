package com.cict.core.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import static com.cict.core.util.IdConverter.toId;

@NoRepositoryBean
public interface BaseRepositoryV2<T, P> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

    /**
     * get entity by string ID
     * @param id String encrypted id
     * @return
     */
    default T getById(String id) {
        return getById(toId(id));
    }

    /**
     *
     * @param id String encrypted id
     * @return
     */
    default Optional<T> findById(String id) {
        return findById(toId(id));
    }

    /**
     * find by ID and SBU this will also show the items on general/public SBU
     * @param sbuId
     * @param id
     * @param active
     * @return
     */
    @Query("Select t from #{#entityName} t where (t.sbuId = t.sbuId AND t.sbuId = :sbuId) and t.active = :active and t.id = :id " )
    Optional<T> findById(@Param("sbuId") long sbuId, @Param("id") long id, @Param("active") boolean active );


    @Query("Select t from #{#entityName} t where (t.sbuId = :sbuId) and t.active =:isActive " )
    Page<T> findAllNoSearch(@Param("sbuId") long sbuId,  @Param("isActive") Boolean isActive, Pageable page);
}
