import Foundation
import Combine

// MARK: Protocol
protocol AuthProviderProtocol {
  func login(email: String, password: String) -> Future<Void, Error>
}

// MARK: Provider
final class AuthProvider: AuthProviderProtocol {
  func login(email: String, password: String) -> Future<Void, Error> {
    return Future<Void, Error> { promise in
      DispatchQueue.global().async {
        promise(.success(()))
      }
    }
  }
}
