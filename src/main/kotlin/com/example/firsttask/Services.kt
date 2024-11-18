package com.example.firsttask

import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal as BigDecimal

interface UserService {
    fun create(request: UserCreateRequest)
    fun getAll(pageable: Pageable): Page<UserResponse>
    fun getOne(id: Long): UserResponse
    fun update(id: Long, request: UserUpdateRequest)
    fun delete(id: Long)
}

interface UserPaymentTransactionService {
    fun fillBalance(fillBalanceDto: FillBalanceDto)
    fun getFillBalanceHistory(userId: Long, pageable: Pageable): Page<FillBalanceHistoryDto>
    fun getOneFillBalance(id: Long): FillBalanceHistoryDto
    fun getFillBalance(pageable: Pageable): Page<FillBalanceHistoryDto>
    fun delete(id: Long)
}

interface CategoryService {
    fun create(categoryDto: CategoryDto)
    fun getAll(pageable: Pageable): Page<CategoryDto>
    fun getOne(id: Long): CategoryDto
    fun update(id: Long, categoryDto: CategoryDto)
    fun delete(id: Long)
}

interface ProductService {
    fun create(createProductDto: CreateProductDto)
    fun getAll(pageable: Pageable): Page<GetProductDto>
    fun getOne(id: Long): GetProductDto
    fun update(id: Long, updateProductDto: UpdateProductDto)
    fun delete(id: Long)
}

interface TransactionService {
    fun create(createTransactionDto: CreateTransactionDto)
    fun getAll(pageable: Pageable): Page<GetTransactionDto>
    fun getOne(id: Long): GetTransactionDto
    fun delete(id: Long)
    fun getAllPurchasedProducts(id: Long, pageable: Pageable): Page<UserProductDto>
    fun getTransactionProducts(transactionId: Long, pageable: Pageable): Page<UserProductDto>
}

interface TransactionItemService {
    fun getAll(pageable: Pageable): Page<GetTransactionItemDto>
    fun getOne(id: Long): GetTransactionItemDto
    fun delete(id: Long)
}

@Service
class UserServiceImp(
    private val userRepository: UserRepository
) : UserService {
    override fun create(request: UserCreateRequest) {
        request.run {
            val user = userRepository.findByUserNameAndDeletedFalse(userName)
            if (user != null)
                throw UserAlreadyExistsException()

            userRepository.save(this.toUser())
        }
    }

    override fun getAll(pageable: Pageable): Page<UserResponse> {
        return userRepository.findAllNotDeletedForPageable(pageable).map {
            UserResponse.toResponse(it)
        }
    }

    override fun getOne(id: Long): UserResponse {
        return userRepository.findByIdAndDeletedFalse(id)?.let {
            UserResponse.toResponse(it)
        } ?: throw UserNotFoundExistsException()
    }


    override fun update(id: Long, request: UserUpdateRequest) {
        val user = userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFoundExistsException()

        request.run {
            userName?.let {
                val usernameAndDeletedFalse = userRepository.findByUsername(id, it)
                if (usernameAndDeletedFalse != null) throw UserAlreadyExistsException()
                user.userName = it
            }
            fullName?.let { user.fullName = it }
            userRole?.let { user.role = it }
        }

        userRepository.save(user)

    }

    override fun delete(id: Long) {
        userRepository.trash(id) ?: throw UserNotFoundExistsException()
    }
}

@Service
class UserPaymentTransactionServiceImpl(
    private val userRepository: UserRepository,
    private val userPaymentTransactionRepository: UserPaymentTransactionRepository
) : UserPaymentTransactionService {

    override fun fillBalance(fillBalanceDto: FillBalanceDto) {
        fillBalanceDto.run {
            val user = userRepository.findByIdAndDeletedFalse(userId) ?: throw UserNotFoundExistsException()

            userPaymentTransactionRepository.save(this.toEntity(user))

            user.balance += amount
            userRepository.save(user)

        }
    }

    override fun getFillBalanceHistory(userId: Long, pageable: Pageable): Page<FillBalanceHistoryDto> {
        userRepository.findByIdAndDeletedFalse(userId) ?: throw UserNotFoundExistsException()

        return userPaymentTransactionRepository.findByUserIdAndDeletedFalse(userId, pageable)?.map {
            FillBalanceHistoryDto.toEntity(it)
        } ?: throw NotFoundExistsTransactionException()

    }

    override fun getFillBalance(pageable: Pageable): Page<FillBalanceHistoryDto> {
        return userPaymentTransactionRepository.findAllNotDeletedForPageable(pageable).map {
            FillBalanceHistoryDto.toEntity(it)
        }
    }

    override fun getOneFillBalance(id: Long): FillBalanceHistoryDto {
        return userPaymentTransactionRepository.findByIdAndDeletedFalse(id)?.let {
            FillBalanceHistoryDto.toEntity(it)
        } ?: throw NotFoundExistsTransactionException()
    }

    override fun delete(id: Long) {
        userPaymentTransactionRepository.trash(id) ?: throw NotFoundExistsTransactionException()
    }

}

@Service
class CategoryServiceImpl(
    private val repository: CategoryRepository
) : CategoryService {
    override fun create(categoryDto: CategoryDto) {
        categoryDto.run {
            repository.save(this.toEntity())
        }
    }

    override fun getAll(pageable: Pageable): Page<CategoryDto> {
        return repository.findAllByOrderByOrderAsc(pageable).map {
            CategoryDto.toCategoryDto(it)
        }
    }

    override fun getOne(id: Long): CategoryDto {
        return repository.findByIdAndDeletedFalse(id)?.let {
            CategoryDto.toCategoryDto(it)
        } ?: throw CategoryNotFoundException()
    }


    override fun update(id: Long, categoryDto: CategoryDto) {
        val category = repository.findByIdAndDeletedFalse(id) ?: throw CategoryNotFoundException()

        categoryDto.run {
            name?.let { category.name = it }
            order?.let { category.order = it }
            description?.let { category.description = it }
        }
        repository.save(category)
    }

    override fun delete(id: Long) {
        repository.trash(id) ?: throw CategoryNotFoundException()
    }

}

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository,
    private val entityManager: EntityManager
) : ProductService {

    override fun create(createProductDto: CreateProductDto) {
        createProductDto.run {
            val category = categoryRepository.findByIdAndDeletedFalse(categoryId) ?: throw CategoryNotFoundException()

            productRepository.save(this.toEntity(category))
        }

    }

    override fun getAll(pageable: Pageable): Page<GetProductDto> {
        return productRepository.findAllNotDeletedForPageable(pageable).map {
            GetProductDto.getDto(it)
        }
    }

    override fun getOne(id: Long): GetProductDto {
        return productRepository.findByIdAndDeletedFalse(id)?.let {
            GetProductDto.getDto(it)
        } ?: throw ProductNotFoundException()
    }

    override fun update(id: Long, updateProductDto: UpdateProductDto) {
        val product = productRepository.findByIdAndDeletedFalse(id) ?: throw ProductNotFoundException()

        updateProductDto.run {
            name?.let { product.name = it }
            count?.let { product.count = it }
            categoryId?.let {
                val category = entityManager.getReference(Category::class.java, it)
                product.category = category
            }
        }
        productRepository.save(product)

    }

    override fun delete(id: Long) {
        productRepository.trash(id) ?: throw ProductNotFoundException()
    }

}


@Service
class TransactionServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val transactionItemRepository: TransactionItemRepository,
    private val productRepository: ProductRepository
//    private val entityManager: EntityManager
) : TransactionService {

    override fun create(createTransactionDto: CreateTransactionDto) {
        createTransactionDto.run {
//            val user = entityManager.getReference(User::class.java, userId)
            val user = userRepository.findByIdAndDeletedFalse(userId) ?: throw UserNotFoundExistsException()

            var totalAmount = BigDecimal.ZERO

            for (transactionItem in transactionItems) {
                totalAmount = totalAmount.add(transactionItem.amount.multiply(transactionItem.count.toBigDecimal()))
            }

            if (user.balance < totalAmount) throw NotEnoughBalanceException()

            val transaction = transactionRepository.save(this.toEntity(user, totalAmount))

            transactionItems.forEach {
                val product =
                    productRepository.findByIdAndDeletedFalse(it.productId) ?: throw ProductNotFoundException()
                product.let { p ->
                    if (p.count < it.count) throw NotEnoughProductException()
                    val transactionItem = TransactionItem(
                        p,
                        it.count,
                        it.amount,
                        it.amount.multiply(it.count.toBigDecimal()),
                        transaction
                    )
                    transactionItemRepository.save(transactionItem)
                }
            }


        }
    }

    override fun getAll(pageable: Pageable): Page<GetTransactionDto> {
        return transactionRepository.findAllNotDeletedForPageable(pageable).map {
            GetTransactionDto.toDto(it)
        }
    }

    override fun getOne(id: Long): GetTransactionDto {
        return transactionRepository.findByIdAndDeletedFalse(id)?.let { GetTransactionDto.toDto(it) }
            ?: throw TransactionNotFoundException()
    }

    override fun delete(id: Long) {
        productRepository.trash(id) ?: throw TransactionNotFoundException()
    }


    override fun getAllPurchasedProducts(id: Long, pageable: Pageable): Page<UserProductDto> {
        return transactionRepository.findUserPurchaseHistory(id, pageable)
    }

    override fun getTransactionProducts(transactionId: Long, pageable: Pageable): Page<UserProductDto> {
        return transactionRepository.findTransactionProducts(transactionId, pageable)

    }


}

@Service
class TransactionItemServiceImpl(
    private val transactionItemRepository: TransactionItemRepository
) : TransactionItemService {
    override fun getAll(pageable: Pageable): Page<GetTransactionItemDto> {
        return transactionItemRepository.findAllNotDeletedForPageable(pageable).map {
            GetTransactionItemDto.toDto(it)
        }
    }

    override fun getOne(id: Long): GetTransactionItemDto {
        return transactionItemRepository.findByIdAndDeletedFalse(id)?.let {
            GetTransactionItemDto.toDto(it)
        } ?: throw TransactionItemNotFoundException()
    }

    override fun delete(id: Long) {
        transactionItemRepository.trash(id) ?: throw TransactionItemNotFoundException()
    }

}

