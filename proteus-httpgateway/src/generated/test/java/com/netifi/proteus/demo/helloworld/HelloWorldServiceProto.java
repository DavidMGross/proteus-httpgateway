// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: proteus/helloworld.proto

package com.netifi.proteus.demo.helloworld;

public final class HelloWorldServiceProto {
  private HelloWorldServiceProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_netifi_proteus_demo_helloworld_HelloRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_netifi_proteus_demo_helloworld_HelloRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_netifi_proteus_demo_helloworld_HelloResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_netifi_proteus_demo_helloworld_HelloResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\030proteus/helloworld.proto\022\"com.netifi.p" +
      "roteus.demo.helloworld\032\033google/protobuf/" +
      "empty.proto\032\025rsocket/options.proto\032\025prot" +
      "eus/gateway.proto\"\034\n\014HelloRequest\022\014\n\004nam" +
      "e\030\001 \001(\t\" \n\rHelloResponse\022\017\n\007message\030\001 \001(" +
      "\t2\331\007\n\021HelloWorldService\022q\n\010SayHello\0220.co" +
      "m.netifi.proteus.demo.helloworld.HelloRe" +
      "quest\0321.com.netifi.proteus.demo.hellowor" +
      "ld.HelloResponse\"\000\022\202\001\n\017SayHelloWithUrl\0220" +
      ".com.netifi.proteus.demo.helloworld.Hell" +
      "oRequest\0321.com.netifi.proteus.demo.hello" +
      "world.HelloResponse\"\n\202\265\030\006\n\004/say\022\212\001\n\025Stre" +
      "amResponseWithUrl\0220.com.netifi.proteus.d" +
      "emo.helloworld.HelloRequest\0321.com.netifi" +
      ".proteus.demo.helloworld.HelloResponse\"\n" +
      "\202\265\030\006\n\004/say0\001\022\205\001\n\016ChannelWithUrl\0220.com.ne" +
      "tifi.proteus.demo.helloworld.HelloReques" +
      "t\0321.com.netifi.proteus.demo.helloworld.H" +
      "elloResponse\"\n\202\265\030\006\n\004/say(\0010\001\022\221\001\n\023SayHell" +
      "oWithTimeout\0220.com.netifi.proteus.demo.h" +
      "elloworld.HelloRequest\0321.com.netifi.prot" +
      "eus.demo.helloworld.HelloResponse\"\025\202\265\030\n\n" +
      "\010/timeout\202\265\030\003\020\320\017\022\235\001\n\031SayHelloWithMaxConc" +
      "urrent\0220.com.netifi.proteus.demo.hellowo" +
      "rld.HelloRequest\0321.com.netifi.proteus.de" +
      "mo.helloworld.HelloResponse\"\033\202\265\030\n\n\010/time" +
      "out\202\265\030\003\020\320\017\202\265\030\002\030\n\022r\n\023SayHelloToEmptyRoom\022" +
      "0.com.netifi.proteus.demo.helloworld.Hel" +
      "loRequest\032\026.google.protobuf.Empty\"\021\202\265\030\010\n" +
      "\006/empty\212B\002\010\001\032\017\202\265\030\013\n\t/v1/helloBN\n\"com.net" +
      "ifi.proteus.demo.helloworldB\026HelloWorldS" +
      "erviceProtoP\001\202\265\030\014\n\nhelloGroupb\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.protobuf.EmptyProto.getDescriptor(),
          io.rsocket.rpc.RSocketOptions.getDescriptor(),
          com.netifi.proteus.gateway.ProteusOptions.getDescriptor(),
        }, assigner);
    internal_static_com_netifi_proteus_demo_helloworld_HelloRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_netifi_proteus_demo_helloworld_HelloRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_netifi_proteus_demo_helloworld_HelloRequest_descriptor,
        new java.lang.String[] { "Name", });
    internal_static_com_netifi_proteus_demo_helloworld_HelloResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_com_netifi_proteus_demo_helloworld_HelloResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_netifi_proteus_demo_helloworld_HelloResponse_descriptor,
        new java.lang.String[] { "Message", });
    com.google.protobuf.ExtensionRegistry registry =
        com.google.protobuf.ExtensionRegistry.newInstance();
    registry.add(io.rsocket.rpc.RSocketOptions.options);
    registry.add(com.netifi.proteus.gateway.ProteusOptions.global);
    registry.add(com.netifi.proteus.gateway.ProteusOptions.method);
    registry.add(com.netifi.proteus.gateway.ProteusOptions.service);
    com.google.protobuf.Descriptors.FileDescriptor
        .internalUpdateFileDescriptor(descriptor, registry);
    com.google.protobuf.EmptyProto.getDescriptor();
    io.rsocket.rpc.RSocketOptions.getDescriptor();
    com.netifi.proteus.gateway.ProteusOptions.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
