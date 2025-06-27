# Start from a basic, stable Ubuntu image
FROM mcr.microsoft.com/devcontainers/base:ubuntu-22.04

# Install necessary tools: Java Development Kit, wget for downloading, and unzip
RUN apt-get update && apt-get install -y openjdk-17-jdk wget unzip

# Set the JAVA_HOME environment variable permanently
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# Download and set up the Android SDK Command Line Tools
ENV ANDROID_SDK_VERSION=11076708
WORKDIR /opt
RUN wget -q https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_SDK_VERSION}_latest.zip -O android-sdk.zip && \
    unzip -q android-sdk.zip -d android-sdk-temp && \
    mkdir -p /opt/android-sdk/cmdline-tools && \
    mv android-sdk-temp/cmdline-tools /opt/android-sdk/cmdline-tools/latest && \
    rm -rf android-sdk.zip android-sdk-temp

# Set the Android environment variables permanently
ENV ANDROID_HOME=/opt/android-sdk
ENV ANDROID_SDK_ROOT=/opt/android-sdk
ENV PATH=$PATH:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools

# Use the new SDK tools to install the required platform and build tools, and accept licenses
RUN yes | sdkmanager --licenses > /dev/null && \
    sdkmanager "platforms;android-33" "build-tools;33.0.2" > /dev/null

# Switch back to the main workspace directory
WORKDIR /workspaces
