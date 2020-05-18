import Foundation
import Combine

protocol AuthProviderProtocol {
  func login(email: String, password: String) -> AnyPublisher<User, Error>
}
