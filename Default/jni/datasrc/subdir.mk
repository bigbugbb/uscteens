################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../jni/datasrc/DataSource.cpp \
../jni/datasrc/DataSourceJNI.cpp 

OBJS += \
./jni/datasrc/DataSource.o \
./jni/datasrc/DataSourceJNI.o 

CPP_DEPS += \
./jni/datasrc/DataSource.d \
./jni/datasrc/DataSourceJNI.d 


# Each subdirectory must supply rules for building sources it contributes
jni/datasrc/%.o: ../jni/datasrc/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -I/Develop/Android/android-ndk-r8e/platforms/android-9/arch-arm/usr/include -I/Develop/Android/android-ndk-r8e/sources/cxx-stl/gnu-libstdc++/4.7/include -O2 -g -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


