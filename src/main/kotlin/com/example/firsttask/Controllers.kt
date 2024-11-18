package com.example.firsttask

import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandler(private val errorMessageSource: ResourceBundleMessageSource) {

    @ExceptionHandler(TaskExceptionHandler::class)
    fun handleAccountException(exception: TaskExceptionHandler): ResponseEntity<BaseMessage> {
        return ResponseEntity.badRequest().body(exception.getErrorMessage(errorMessageSource))
    }
}


@RestController
@RequestMapping("api/user")
class UserController(
    private val service: UserService
) {

    @PostMapping("create")
    fun create(@RequestBody request: UserCreateRequest) = service.create(request)

    @GetMapping
    fun getAll(pageable: Pageable) = service.getAll(pageable)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long) = service.getOne(id)

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UserUpdateRequest) = service.update(id, request)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

}

@RestController
@RequestMapping("api/payment")
class UserPaymentTransactionController(
    private val service: UserPaymentTransactionService
) {

    //2-task
    @PostMapping()
    fun fillBalance(@RequestBody fillBalanceDto: FillBalanceDto) =
        service.fillBalance(fillBalanceDto)

    //3-task
    @GetMapping("forUser/{userId}")
    fun getFillBalanceHistory(@PathVariable userId: Long, pageable: Pageable) =
        service.getFillBalanceHistory(userId, pageable)

    @GetMapping()
    fun getFillBalance(pageable: Pageable) =
        service.getFillBalance(pageable)

    @GetMapping("{id}")
    fun getOneFillBalance(@PathVariable id: Long) = service.getOneFillBalance(id)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

}

@RestController
@RequestMapping("api/category")
class CategoryController(
    private val service: CategoryService
) {

    @PostMapping()
    fun create(@RequestBody categoryDto: CategoryDto) =
        service.create(categoryDto)

    @GetMapping("{id}")
    fun getOneCategory(@PathVariable id: Long) =
        service.getOne(id)

    @GetMapping()
    fun getAllCategory(pageable: Pageable) =
        service.getAll(pageable)

    @PutMapping("{id}")
    fun getOneFillBalance(@PathVariable id: Long, @RequestBody categoryDto: CategoryDto) =
        service.update(id, categoryDto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

}

@RestController
@RequestMapping("api/product")
class ProductController(
    private val service: ProductService
) {

    @PostMapping()
    fun create(@RequestBody createProductDto: CreateProductDto) =
        service.create(createProductDto)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long) =
        service.getOne(id)

    @GetMapping()
    fun getAll(pageable: Pageable) =
        service.getAll(pageable)

    @PutMapping("{id}")
    fun getOneFillBalance(@PathVariable id: Long, @RequestBody updateProductDto: UpdateProductDto) =
        service.update(id, updateProductDto)

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

}

@RestController
@RequestMapping("api/transaction")
class TransactionController(
    private val service: TransactionService
) {

    @PostMapping()
    fun create(@RequestBody createTransactionDto: CreateTransactionDto) =
        service.create(createTransactionDto)

    @GetMapping("{id}")
    fun getOne(@PathVariable id: Long) =
        service.getOne(id)

    //6-tak
    @GetMapping()
    fun getAll(pageable: Pageable) =
        service.getAll(pageable)


    //4-task
    @GetMapping("forUser/{id}")
    fun getAllPurchasedProducts(@PathVariable id: Long, pageable: Pageable) =
        service.getAllPurchasedProducts(id, pageable)


    //-5
    @GetMapping("/{transactionId}/products")
    fun getTransactionProducts(@PathVariable transactionId: Long, pageable: Pageable) =
        service.getTransactionProducts(transactionId, pageable)


    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)

}