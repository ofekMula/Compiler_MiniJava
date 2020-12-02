@.Classes_vtable = global [1 x i8*] [i8* bitcast (i32 (i8*)* @Classes.run to i8*)]

@.Base_vtable = global [2 x i8*] [
	i8* bitcast (i32 (i8*, i32)* @Base.set to i8*),
	i8* bitcast (i32 (i8*)* @Base.get to i8*)
]

@.Derived_vtable = global [2 x i8*] [
	i8* bitcast (i32 (i8*, i32)* @Derived.set to i8*), 
	i8* bitcast (i32 (i8*)* @Base.get to i8*)
]

declare i8* @calloc(i32, i32)
declare i32 @printf(i8*, ...)
declare void @exit(i32)

@_cint = constant [4 x i8] c"%d\0a\00"
@_cOOB = constant [15 x i8] c"Out of bounds\0a\00"
define void @print_int(i32 %i) {
	%_str = bitcast [4 x i8]* @_cint to i8*
	call i32 (i8*, ...) @printf(i8* %_str, i32 %i)
	ret void
}

define void @throw_oob() {
	%_str = bitcast [15 x i8]* @_cOOB to i8*
	call i32 (i8*, ...) @printf(i8* %_str)
	call void @exit(i32 1)
	ret void
}


define i32 @main() {
	%_0 = call i8* @calloc(i32 1, i32 8)
	%_1 = bitcast i8* %_0 to i8***
	%_2 = getelementptr [1 x i8*], [1 x i8*]* @.Classes_vtable, i32 0, i32 0
	store i8** %_2, i8*** %_1
	%_3 = bitcast i8* %_0 to i8***
	%_4 = load i8**, i8*** %_3
	%_5 = getelementptr i8*, i8** %_4, i32 0
	%_6 = load i8*, i8** %_5
	%_7 = bitcast i8* %_6 to i32 (i8*)*
	%_8 = call i32 %_7(i8* %_0)
	call void (i32) @print_int(i32 %_8)
	ret i32 0
}

define i32 @Classes.run(i8* %this) {
	%b = alloca i8*	
	%d = alloca i8*	

    ; The following sequence of instructions creates a new Base object

    ; First, we allocate the required memory on heap for our object.
    ; We call calloc to achieve this: 
    ;   * The first argument is the amount of objects we want to allocate
    ;     (always 1 for object allocation, but handy in arrays)
    ;   * The second argument is the size of the object. This is calculated as the sum of the
    ;     size of the fields of the class and all the super classes PLUS 8 bytes, to account for
    ;     the vtable pointer.
    ; In our case, we have a single int field so it's 4 + 8 = 12 bytes
	%_0 = call i8* @calloc(i32 1, i32 12)

	; Next we need to set the vtable pointer to point to the correct vtable (Base_vtable)
    ; First we bitcast the object pointer from i8* to i8***
    ; This is done because:
    ;   -> The vtable stores values of type i8*.
    ;   -> Thus, a pointer that points to the start of the vtable (equivalently at the first entry
    ;      of the vtable) must have type i8**.
    ;   -> Thus, to set the vtable pointer at the start of the object, we need to have its address
    ;      (first byte of the object) in a register of type i8*** 
    ;		- it's a pointer to a location where we will be storing i8**.
	%_1 = bitcast i8* %_0 to i8***

	; Get the address of the first element of the Base_vtable
    ; The getelementptr arguments are as follows:
    ;   * The first argument is the type of elements our Base_vtable ptr points to.
    ;   * The second argument is the Base_vtable ptr.
    ;   * The third and fourth arguments are indexes
    ;; (alternative to getelementpr: %_2 = bitcast [2 x i8*]* @.Base_vtable to i8**)
	%_2 = getelementptr [2 x i8*], [2 x i8*]* @.Base_vtable, i32 0, i32 0

	; Set the vtable to the correct address.
	store i8** %_2, i8*** %_1

	; Store the address of the new object on the stack (var b), as a byte array (i8*).
	store i8* %_0, i8** %b	

	; Same process, use Derived_vtable this time
	%_3 = call i8* @calloc(i32 1, i32 12)
	%_4 = bitcast i8* %_3 to i8***
	%_5 = getelementptr [2 x i8*], [2 x i8*]* @.Derived_vtable, i32 0, i32 0
	store i8** %_5, i8*** %_4
	store i8* %_3, i8** %d	


	; This segment implements the b.set(1) call

	; First load the object pointer from the stack variable for b
	%_6 = load i8*, i8** %b

	; Do the required bitcasts, so that we can access the vtable pointer - we're holding a pointer to i8**
	%_7 = bitcast i8* %_6 to i8***

	; Load vtable_ptr
	%_8 = load i8**, i8*** %_7

	; Get a pointer to the 0-th entry in the vtable. 
	; The index here is exactly the offset corresponding to Base::set.
	%_9 = getelementptr i8*, i8** %_8, i32 0

	; Read into the array to get the actual function pointer
	%_10 = load i8*, i8** %_9

	; Cast the function pointer from i8* to a function ptr type that matches the function's signature,
	; so that we can call it.
	%_11 = bitcast i8* %_10 to i32 (i8*, i32)*

	; Perform the call on the function pointer. Note that the first argument is the receiver object ("this").
	%_12 = call i32 %_11(i8* %_6, i32 1)

	; Call print with the return value as argument
	call void (i32) @print_int(i32 %_12)

	; Load d and store it to b
	%_13 = load i8*, i8** %d
	store i8* %_13, i8** %b	

	; Now the same process for the second call, this time on the Derived object
	; This code is *exactly* the same as before because the static type is the same!
	%_14 = load i8*, i8** %b
	%_15 = bitcast i8* %_14 to i8***
	%_16 = load i8**, i8*** %_15
	%_17 = getelementptr i8*, i8** %_16, i32 0
	%_18 = load i8*, i8** %_17
	%_19 = bitcast i8* %_18 to i32 (i8*, i32)*
	%_20 = call i32 %_19(i8* %_14, i32 3)
	call void (i32) @print_int(i32 %_20)

	; Now calling Derived::get - note the different offset in the vtable
	%_21 = load i8*, i8** %d
	%_22 = bitcast i8* %_21 to i8***
	%_23 = load i8**, i8*** %_22
	; offset 1 in the vtable
	%_24 = getelementptr i8*, i8** %_23, i32 1
	%_25 = load i8*, i8** %_24
	%_26 = bitcast i8* %_25 to i32 (i8*)*
	%_27 = call i32 %_26(i8* %_21)
	call void (i32) @print_int(i32 %_27)
	ret i32 0	
}


define i32 @Base.set(i8* %this, i32 %.x) {
	; Allocate formal parameter on the stack
	; (to support code the assigns to the formal parameter)
	%x = alloca i32
	store i32 %.x, i32* %x	

	; Read the value from the formal parameter
	%_0 = load i32, i32* %x

	; Get pointer to the byte where the field starts
	; Here the index is 8 because we're accessing the first field,
	; which resides immediately after the vtable pointer
	%_1 = getelementptr i8, i8* %this, i32 8

	; Cast to a pointer to the field with the correct type
	%_2 = bitcast i8* %_1 to i32*

	; Write to the field
	store i32 %_0, i32* %_2	

	; Now the same process for reading the field (load instead of store in the end)
	%_3 = getelementptr i8, i8* %this, i32 8
	%_4 = bitcast i8* %_3 to i32*
	%_5 = load i32, i32* %_4

	ret i32 %_5	
}

define i32 @Base.get(i8* %this) {
	%_0 = getelementptr i8, i8* %this, i32 8
	%_1 = bitcast i8* %_0 to i32*
	%_2 = load i32, i32* %_1
	ret i32 %_2	
}


define i32 @Derived.set(i8* %this, i32 %.x) {
	%x = alloca i32	
	store i32 %.x, i32* %x	
	%_0 = load i32, i32* %x
	%_1 = mul i32 %_0, 2
	%_2 = getelementptr i8, i8* %this, i32 8
	%_3 = bitcast i8* %_2 to i32*
	store i32 %_1, i32* %_3	
	%_4 = getelementptr i8, i8* %this, i32 8
	%_5 = bitcast i8* %_4 to i32*
	%_6 = load i32, i32* %_5
	ret i32 %_6	
}

