package com.example.firsttask

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@NoRepositoryBean
interface BaseRepository<T : BaseEntity> : JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    fun findByIdAndDeletedFalse(id: Long): T?
    fun trash(id: Long): T?
    fun trashList(ids: List<Long>): List<T?>
    fun findAllNotDeleted(): List<T>
    fun findAllNotDeleted(pageable: Pageable): List<T>
    fun findAllNotDeletedForPageable(pageable: Pageable): Page<T>
    fun saveAndRefresh(t: T): T
}

class BaseRepositoryImpl<T : BaseEntity>(
    entityInformation: JpaEntityInformation<T, Long>,
    private val entityManager: EntityManager
) : SimpleJpaRepository<T, Long>(entityInformation, entityManager), BaseRepository<T> {

    val isNotDeletedSpecification = Specification<T> { root, _, cb -> cb.equal(root.get<Boolean>("deleted"), false) }

    override fun findByIdAndDeletedFalse(id: Long) = findByIdOrNull(id)?.run { if (deleted) null else this }

    @Transactional
    override fun trash(id: Long): T? = findByIdOrNull(id)?.run {
        deleted = true
        save(this)
    }

    override fun findAllNotDeleted(): List<T> = findAll(isNotDeletedSpecification)
    override fun findAllNotDeleted(pageable: Pageable): List<T> = findAll(isNotDeletedSpecification, pageable).content
    override fun findAllNotDeletedForPageable(pageable: Pageable): Page<T> =
        findAll(isNotDeletedSpecification, pageable)

    override fun trashList(ids: List<Long>): List<T?> = ids.map { trash(it) }

    @Transactional
    override fun saveAndRefresh(t: T): T {
        return save(t).apply { entityManager.refresh(this) }
    }
}

@Repository
interface UserRepository : BaseRepository<User> {
    fun findByUserNameAndDeletedFalse(userName: String): User?

    @Query(
        """
        select u from users u 
        where u.id != :id
        and u.userName = :userName
        and u.deleted = false 
    """
    )
    fun findByUsername(id: Long, userName: String): User?

}

@Repository
interface CategoryRepository : BaseRepository<Category>

@Repository
interface ProductRepository : BaseRepository<Product>

@Repository
interface UserPaymentTransactionRepository : BaseRepository<UserPaymentTransaction> {
    fun findByUserIdAndDeletedFalse(userId:Long, pageable: Pageable):Page<UserPaymentTransaction>?
}

@Repository
interface TransactionItemRepository : BaseRepository<TransactionItem>

@Repository
interface TransactionRepository : BaseRepository<Transaction>{


    @Query("""
    SELECT 
        u.userName AS userName, 
        p.name AS productName, 
        ti.count AS count, 
        ti.amount AS amount, 
        ti.total_amount AS totalAmount
    FROM products p 
    JOIN transaction_items ti ON p.id = ti.product.id
    JOIN transactions t ON ti.transaction.id = t.id
    JOIN users u ON t.user.id = u.id
    WHERE t.user.id = :userId
""")
    fun findUserPurchaseHistory(@Param("userId") userId: Long, pageable: Pageable): Page<UserProductDto>


    @Query("""
        SELECT 
            u.userName AS userName, 
            p.name AS productName, 
            ti.count AS count, 
            ti.amount AS amount, 
            ti.total_amount AS totalAmount
    FROM products p 
    JOIN transaction_items ti ON p.id = ti.product.id
    JOIN transactions t ON ti.transaction.id = t.id
    JOIN users u ON t.user.id = u.id
    WHERE t.id = :transactionId
    """)
    fun findTransactionProducts(@Param("transactionId") transactionId: Long, pageable: Pageable): Page<UserProductDto>


}




