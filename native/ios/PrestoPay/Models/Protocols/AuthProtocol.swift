import Foundation
import Combine

protocol AuthProviderProtocol {
  func login(email: String, password: String) -> AnyPublisher<User, Error>
  func signUp(
    name: String,
    username: String,
    email: String,
    thumbnail: String,
    password: String,
    passwordConfirmation: String
  ) -> AnyPublisher<User, Error>
}
