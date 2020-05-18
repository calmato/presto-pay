import Foundation
import Combine

final class LoginViewModel: ObservableObject {
  // MARK: Private
  private let authProvider: AuthProviderProtocol
  private var cancellable: AnyCancellable?

  // MARK: Input
  @Published var email: String = ""
  @Published var password: String = ""
  
  // MARK: Output
  @Published private(set) var validationError: String = ""

  // MARK: Initializer
  init (authProvider: AuthProviderProtocol = AuthProvider()) {
    self.authProvider = authProvider
  }

  // MARK: Action
  func login() {
    validationError = "test"
    
    cancellable = authProvider.login(email: email, password: password)
      .receive(on: RunLoop.main)
      .sink(receiveCompletion: { completion in
        switch completion {
        case .failure:
          self.validationError = "Error" // TODO: Edit
        case .finished:
          self.validationError = "Success" // TODO: Edit
        }
      }, receiveValue: { value in
        print("value: \(value)")
      })
  }
}
