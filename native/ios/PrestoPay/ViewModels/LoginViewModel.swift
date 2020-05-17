import Foundation
import Combine

final class LoginViewModel: ObservableObject {
  // MARK: Input
  @Published var email: String = ""
  @Published var password: String = ""
}
