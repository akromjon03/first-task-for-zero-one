package com.example.firsttask

import jakarta.persistence.ManyToOne
import java.math.BigDecimal
import java.util.Date

data class UserCreateRequest(
    val fullName: String,
    val userName: String
) {
    fun toUser(): User {
        return User(fullName, userName, UserRole.USER, 0.toBigDecimal())
    }
}

data class UserResponse(
    val id: Long,
    val fullName: String,
    val userName: String,
    val userRole: String,
    val balance: BigDecimal
) {
    companion object {
        fun toResponse(user: User): UserResponse {
            user.run {
                return UserResponse(id!!, fullName, userName, userName, balance)
            }
        }
    }
}

data class UserUpdateRequest(
    val fullName: String?,
    val userName: String?,
    val userRole: UserRole?,
)

data class BaseMessage(
    val code: Int,
    val message: String?
)

data class FillBalanceDto(
    val userId: Long,
    val amount: BigDecimal
) {
    fun toEntity(user: User): UserPaymentTransaction {
        return UserPaymentTransaction(user, amount, Date())
    }
}

data class FillBalanceHistoryDto(
    val id: Long,
    val userId: Long,
    val amount: BigDecimal,
    val date: Date
) {
    companion object {
        fun toEntity(it: UserPaymentTransaction): FillBalanceHistoryDto {
            it.run {
                return FillBalanceHistoryDto(id!!, user.id!!, amount, date)
            }
        }
    }
}

data class CategoryDto(
    val name: String?,
    val order: Long?,
    val description: String?
) {
    fun toEntity(): Category {
        return Category(name!!, order!!, description!!)
    }

    companion object {
        fun toCategoryDto(it: Category): CategoryDto {
            it.run { return CategoryDto(name, order, description) }
        }
    }

}

data class CreateProductDto(
    val name: String,
    val count: Long,
    val categoryId: Long
) {
    fun toEntity(category: Category): Product {
        return Product(name, count, category)
    }
}

data class UpdateProductDto(
    val name: String?,
    val count: Long?,
    val categoryId: Long?
)

data class GetProductDto(
    val id: Long,
    val name: String,
    val count: Long,
    val categoryId: Long
) {
    companion object {
        fun getDto(it: Product): GetProductDto {
            it.run {
                return GetProductDto(id!!, name, count, category.id!!)
            }
        }
    }
}

data class CreateTransactionDto(
    val userId: Long,
    val transactionItems: MutableList<CreateTransactionItemDto>
) {
    fun toEntity(user: User, totalAmount: BigDecimal): Transaction {
        return Transaction(user, totalAmount, Date())
    }
}

data class GetTransactionDto(
    val id: Long,
    val userId: Long,
    val totalAmount: BigDecimal,
    val date: Date
) {
    companion object {
        fun toDto(it: Transaction): GetTransactionDto {
            it.run {
                return GetTransactionDto(id!!, user.id!!, total_amount, date)
            }

        }
    }
}

data class CreateTransactionItemDto(
    val productId: Long,
    val count: Long,
    val amount: BigDecimal,
    val transaction: Transaction
)

data class GetTransactionItemDto(
    val id: Long,
    val productId: Long,
    val count: Long,
    val amount: BigDecimal,
    val totalAmount: BigDecimal,
    val transactionId: Long
) {
    companion object {
        fun toDto(it: TransactionItem): GetTransactionItemDto {
            it.run {
                return GetTransactionItemDto(id!!, product.id!!, count, amount, total_amount, transaction.id!!)
            }
        }
    }

}

interface UserProductDto {
    val userName: String
    val productName: String
    val count: Long
    val amount: BigDecimal
    val totalAmount: BigDecimal
}














