// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: proteus/endpoint.proto

package com.netifi.proteus.httpgateway.endpoint.source;

public final class EndpointService {
  private EndpointService() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_proteus_gateway_ProtoDescriptor_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_proteus_gateway_ProtoDescriptor_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\026proteus/endpoint.proto\022\017proteus_gatewa" +
      "y\032\033google/protobuf/empty.proto\"\241\001\n\017Proto" +
      "Descriptor\022\014\n\004name\030\001 \001(\t\022\027\n\017descriptorBy" +
      "tes\030\002 \001(\014\0228\n\004type\030\003 \001(\0162*.proteus_gatewa" +
      "y.ProtoDescriptor.EventType\"-\n\tEventType" +
      "\022\007\n\003ADD\020\000\022\013\n\007REPLACE\020\001\022\n\n\006DELETE\020\0022h\n\016En" +
      "dpointSource\022V\n\026StreamProtoDescriptors\022\026" +
      ".google.protobuf.Empty\032 .proteus_gateway" +
      ".ProtoDescriptor\"\0000\001BC\n.com.netifi.prote" +
      "us.httpgateway.endpoint.sourceB\017Endpoint" +
      "ServiceP\001b\006proto3"
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
        }, assigner);
    internal_static_proteus_gateway_ProtoDescriptor_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_proteus_gateway_ProtoDescriptor_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_proteus_gateway_ProtoDescriptor_descriptor,
        new java.lang.String[] { "Name", "DescriptorBytes", "Type", });
    com.google.protobuf.EmptyProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}