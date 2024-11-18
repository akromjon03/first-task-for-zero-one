package com.example.firsttask

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.math.BigDecimal
import java.util.Date

@MappedSuperclass
class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false
)

@Entity(name = "users")
class User(
    @Column(nullable = false) var fullName: String,
    @Column(nullable = false, unique = true) var userName: String,
    @Enumerated(EnumType.STRING) var role: UserRole,
    var balance: BigDecimal

) : BaseEntity()

@Entity(name = "products")
class Product(
    @Column(nullable = false) var name: String,
    var count: Long,
    @ManyToOne var category: Category

) : BaseEntity()

@Entity(name = "categories")
class Category(
    @Column(nullable = false) var name: String,
    @Column(name="order-number") var order: Long,
    var description: String

) : BaseEntity()

@Entity(name = "transactions")
class Transaction(
    @ManyToOne val user: User,
    val total_amount: BigDecimal,
    val date: Date

) : BaseEntity()

@Entity(name = "transaction_items")
class TransactionItem(
    @ManyToOne val product:Product,
    val count:Long,
    val amount: BigDecimal,
    val total_amount: BigDecimal,
    @ManyToOne val transaction: Transaction

) : BaseEntity()

@Entity(name = "user-payment-transaction")
class UserPaymentTransaction(
    @ManyToOne val user: User,
    val amount: BigDecimal,
    val date: Date

):BaseEntity()