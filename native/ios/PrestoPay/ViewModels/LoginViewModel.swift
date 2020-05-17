import Foundation
import Combine

final class LoginViewModel: ObservableObject {
  // MARK: Private
//  private let authProvider: AuthProviderProtocol
  
  // MARK: Input
  @Published var email: String = ""
  @Published var password: String = ""
  
//  init (authProvider: AuthProviderProtocol = AuthProvider()) {}

  // MARK: Action
//  func login() -> AnyPublisher<Void, Error> {
//    // TODO: Creat AuthProvider
//    return authProvider.login(email: email, password, password)
//      .receive(on: RunLoop.main)
//  }
}
