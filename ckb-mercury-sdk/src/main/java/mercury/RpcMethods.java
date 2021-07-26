package mercury;

public interface RpcMethods {
  String GET_BALANCE = "get_balance";
  String BUILD_TRANSFER_TRANSACTION = "build_transfer_transaction";
  String BUILD_ASSET_ACCOUNT_CREATION_TRANSACTION = "build_asset_account_creation_transaction";
  String GET_GENERIC_TRANSACTION = "get_generic_transaction";
  String GET_GENERIC_BLOCK = "get_generic_block";
  String REGISTER_ADDRESSES = "register_addresses";
  String BUILD_ASSET_COLLECTION_TRANSACTION = "build_asset_collection_transaction";
  String QUERY_GENERIC_TRANSACTIONS = "query_generic_transactions";
}