package com.cict.core.base;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static com.cict.core.util.IdConverter.toId;

@NoRepositoryBean
public interface BaseRepository<T, P> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {

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

    /**
     * find by ID and SBU and Code this will also show the items on general/public SBU
     * @param sbuId
     * @param code
     * @param active
     * @return
     */
    @Query("Select t from #{#entityName} t where (t.sbuId = t.sbuId AND t.sbuId = :sbuId) and t.active = :active and t.code = :code " )
    Optional<T> findBySbuAndCode(@Param("sbuId") long sbuId, @Param("code") String code, @Param("active") boolean active );

    /**
     * count by ID and SBU and Code this will also show the items on general/public SBU
     * @param sbuId
     * @param code
     * @param active
     * @return
     */
    @Query("Select count(t) from #{#entityName} t where (t.sbuId = t.sbuId AND t.sbuId = :sbuId) and t.active = :active and t.code = :code " )
    long countBySbuAndCode(@Param("sbuId") long sbuId, @Param("code") String code, @Param("active") boolean active  );

    /**
     * use by defualt CRUD mechanism to validate if code exist inn the table
     * @param sbuId
     * @param code
     * @return
     */
    @Query("Select t from #{#entityName} t where (t.sbuId = t.sbuId and t.sbuId = :sbuId)  and t.code = :code " )
    Optional<T> validateFindBySbuAndCode(@Param("sbuId") long sbuId, @Param("code") String code );

    /**
     *  use by defualt CRUD mechanism to count if code exist inn the table
     * @param sbuId
     * @param code
     * @return
     */
    @Query("Select count(t) from #{#entityName} t where (t.sbuId = t.sbuId and t.sbuId = :sbuId) and t.code = :code " ) // code is unique by sbu id
    long validateCountBySbuAndCode(@Param("sbuId") long sbuId, @Param("code") String code );

    /**
     * old SQL getDate - service date
     * @return
     */
    @Query(value = "select getdate()", nativeQuery = true)
    Date getServerDateTime();

    /**
     * This is the default find all data use by respository in respective Modules
     * can be overiden with your own searchar query
     * @param sbuId
     * @param search
     * @param isActive
     * @param page
     * @return
     */
//    remove the sbu access default to 1
//    @Query("Select t from #{#entityName} t where (t.sbuId = 1 OR t.sbuId = :sbuId) and t.active =:isActive and ((lower(isnull(t.code,'')) like :search ) or (lower(isnull(t.description,'')) like :search ))" )
//    Page<T> findAllData(@Param("sbuId") long sbuId, @Param("search") String search, @Param("isActive") Boolean isActive, Pageable page);

    @Query("Select t from #{#entityName} t where (t.sbuId = :sbuId) and t.active =:isActive and ((lower(isnull(t.code,'')) like :search ) or (lower(isnull(t.description,'')) like :search ))" )
    Page<T> findAllData(@Param("sbuId") long sbuId, @Param("search") String search, @Param("isActive") Boolean isActive, Pageable page);


    @Query("Select t from #{#entityName} t where (t.sbuId = :sbuId) and t.active =:isActive " )
    Page<T> findAllNoSearch(@Param("sbuId") long sbuId,  @Param("isActive") Boolean isActive, Pageable page);

    /**
     * This is use a a default activator/deactivator use by the core / with date
     * @param id
     * @param username
     * @param timeUpdated
     */
    @Modifying
    @Transactional
    @Query("Update #{#entityName} t set " +
            "t.active = False, " +
            "t.updatedBy = :username, " +
            "t.timeUpdated = :timeUpdated where t.id = :id")
    void setInActive(@Param("id") long id,
                   @Param("username") String username,
                   @Param("timeUpdated") LocalDateTime timeUpdated
                   );

    @Modifying
    @Transactional
    @Query("DELETE FROM #{#entityName} t where t.id = :id")
    void deletePermanent(@Param("id") long id);


    @Modifying
    @Transactional
    @Query("Update #{#entityName} t set " +
            "t.active = true , " +
            "t.updatedBy = :username, " +
            "t.timeUpdated = :timeUpdated where t.id = :id")
    void setActive(long id, String username, LocalDateTime timeUpdated);
}
